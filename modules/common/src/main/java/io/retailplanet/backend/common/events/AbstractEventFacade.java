package io.retailplanet.backend.common.events;

import io.opentracing.*;
import io.reactivex.*;
import io.retailplanet.backend.common.events.exceptions.ErrorEventException;
import io.retailplanet.backend.common.util.EventUtility;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.*;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.function.Supplier;

/**
 * @author w.glanzer, 18.07.2019
 */
public abstract class AbstractEventFacade implements IAbstractEventFacade
{

  /**
   * Flowable for incoming error messages
   */
  @Stream("ERRORS_IN")
  protected Flowable<ErrorEvent> errorsFlowable;

  /**
   * Emitter to emit error messages
   */
  @Stream("ERRORS_OUT")
  protected Emitter<ErrorEvent> errorsEmitter;

  @Inject
  protected Tracer tracer;

  @Override
  public void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull Throwable pThrowable)
  {
    notifyError(pSourceEvent, "", pThrowable);
  }

  @Override
  public void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull String pMessage)
  {
    ErrorEvent event = pSourceEvent == null ? new ErrorEvent() : pSourceEvent.createAnswer(ErrorEvent.class);
    event = event.error(new Exception(pMessage));

    // log
    LoggerFactory.getLogger(getClass()).error("ErrorEvent was raised", new ErrorEventException(pMessage));

    // send
    errorsEmitter.send(event);
  }

  @Override
  public void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull String pMessage, @NotNull Throwable pThrowable)
  {
    ErrorEvent event = pSourceEvent == null ? new ErrorEvent() : pSourceEvent.createAnswer(ErrorEvent.class);
    event = event.error(new Exception(pMessage, pThrowable));

    // log
    LoggerFactory.getLogger(getClass()).error("ErrorEvent was raised", new ErrorEventException(pMessage, pThrowable));

    // send
    errorsEmitter.send(event);
  }

  @Override
  public <Out> Out trace(@NotNull AbstractEvent<?> pEvent, @NotNull Supplier<Out> pFn)
  {
    pEvent.startTrace(tracer);

    try
    {
      return pFn.get();
    }
    finally
    {
      pEvent.finishTrace();
    }
  }

  @PostConstruct
  public void init()
  {
    errorsFlowable = errorsFlowable.as(EventUtility::replayEvents);
  }

  @SafeVarargs
  @NotNull
  protected final <In extends AbstractEvent<In>, Out extends AbstractEvent<Out>> Single<Out> send(@NotNull In pEvent, @NotNull Emitter<In> pEmitter, Flowable<? extends Out>... pAnswerFlowables)
  {
    // inject current span
    pEvent.injectCurrentTrace(tracer);

    // send event
    pEmitter.send(pEvent);

    // No result is expected
    if (pAnswerFlowables.length == 0)
      return Single.never();

    // result expected
    return pEvent.waitForAnswer(errorsFlowable, pAnswerFlowables)
        .map(pEv -> {
          pEv.startTrace(tracer);
          return pEv;
        })
        .doOnError(pEx -> {
          ScopeManager sm = tracer == null ? null : tracer.scopeManager();
          if (sm != null)
          {
            Scope active = sm.active();
            if (active != null)
              active.close();
          }
        })
        .doOnSuccess(AbstractEvent::finishTrace);
  }

}
