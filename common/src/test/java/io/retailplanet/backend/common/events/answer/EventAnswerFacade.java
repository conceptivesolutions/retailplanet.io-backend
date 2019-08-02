package io.retailplanet.backend.common.events.answer;

import io.reactivex.Single;
import io.retailplanet.backend.common.events.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.*;

import java.util.*;
import java.util.function.*;

/**
 * Object which holds information about all mocked answers for an eventfacade
 *
 * @author w.glanzer, 28.07.2019
 */
class EventAnswerFacade implements IEventAnswerFacade.IReadable, IEventAnswerFacade.IWriteable
{

  private static final Map<Object, EventAnswerFacade> _INSTANCES = new HashMap<>();
  private static final Logger _LOGGER = LoggerFactory.getLogger(EventAnswerFacade.class);

  private final Map<Class<? extends AbstractEvent>, Queue<Function<? extends AbstractEvent<?>, AbstractEvent<?>>>> answerQueue = new HashMap<>();

  @NotNull
  static EventAnswerFacade getInstance(@NotNull IAbstractEventFacade pEventFacade)
  {
    String cacheKey = pEventFacade.getClass().getName();
    if (cacheKey.endsWith("_ClientProxy"))
      cacheKey = cacheKey.substring(0, cacheKey.length() - 12);
    return _INSTANCES.computeIfAbsent(cacheKey, pEF -> new EventAnswerFacade());
  }

  private EventAnswerFacade()
  {
  }

  @NotNull
  @Override
  public synchronized <EVENT extends AbstractEvent<EVENT>> EventAnswerFacade answerWith(@NotNull Class<EVENT> pSource, @NotNull AbstractEvent<?>... pReturnEvent)
  {
    EventAnswerFacade returnValue = this;
    for (AbstractEvent<?> returnEvent : pReturnEvent)
      returnValue = returnValue.answerWith(pSource, pE -> returnEvent);
    return returnValue;
  }

  @NotNull
  @Override
  public synchronized <EVENT extends AbstractEvent<EVENT>> EventAnswerFacade answerWith(@NotNull Class<EVENT> pSource, @NotNull Function<EVENT, AbstractEvent<?>> pAnswerCreation)
  {
    answerQueue.computeIfAbsent(pSource, pClazz -> new LinkedList<>()).add(pAnswerCreation);
    return this;
  }

  @NotNull
  @Override
  public synchronized EventAnswerFacade reset()
  {
    _LOGGER.info("Resetting AnswerFacade");
    answerQueue.clear();
    return this;
  }

  @Override
  @NotNull
  public synchronized <T extends AbstractEvent<T>> Single<T> answerEvent(@NotNull AbstractEvent<?> pSourceEvent, @NotNull Supplier<T> pDefaultEvent)
  {
    Queue<Function<? extends AbstractEvent<?>, AbstractEvent<?>>> matchingQueue = answerQueue.get(pSourceEvent.getClass());
    if (matchingQueue != null)
    {
      Function resultFn = matchingQueue.poll();
      if (resultFn != null)
      {
        T result = (T) resultFn.apply(pSourceEvent);
        _LOGGER.info("Answering event " + pSourceEvent + " with " + result);
        return Single.just(result);
      }
    }

    _LOGGER.info("Creating default answer event for source " + pSourceEvent);
    return Single.just(pDefaultEvent.get());
  }

}
