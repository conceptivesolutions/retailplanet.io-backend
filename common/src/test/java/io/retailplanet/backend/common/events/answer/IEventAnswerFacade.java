package io.retailplanet.backend.common.events.answer;

import io.reactivex.Single;
import io.retailplanet.backend.common.events.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.*;

/**
 * Object which holds information about all mocked answers for an eventfacade
 *
 * @author w.glanzer, 02.08.2019
 */
public interface IEventAnswerFacade
{

  /**
   * Retrieve the answerfacade in WRITE mode
   *
   * @param pEventFacade EventFacade, this answerFacade should encapsulate
   * @return Writeable answer facade
   */
  @NotNull
  static IEventAnswerFacade.IWriteable writeAccess(@NotNull IAbstractEventFacade pEventFacade)
  {
    return EventAnswerFacade.getInstance(pEventFacade);
  }

  /**
   * Retrieve the answerfacade in READ mode
   *
   * @param pEventFacade EventFacade, this answerFacade should encapsulate
   * @return Readable answer facade
   */
  @NotNull
  static IEventAnswerFacade.IReadable readAccess(@NotNull IAbstractEventFacade pEventFacade)
  {
    return EventAnswerFacade.getInstance(pEventFacade);
  }

  /**
   * Contains all WRITE methods for this facade
   */
  interface IWriteable
  {

    /**
     * Specify answers for a given source event type
     *
     * @param pSource      Type of events, that should be answered
     * @param pReturnEvent List of events which should be returned. It emits item by item.
     * @return Builder
     */
    @NotNull <EVENT extends AbstractEvent<EVENT>> IWriteable answerWith(@NotNull Class<EVENT> pSource, @NotNull AbstractEvent<?>... pReturnEvent);

    /**
     * Specify answer for a given source event type
     *
     * @param pSource         Type of events, that should be answered
     * @param pAnswerCreation Function to create an answer for the given event
     * @return Builder
     */
    @NotNull <EVENT extends AbstractEvent<EVENT>> IWriteable answerWith(@NotNull Class<EVENT> pSource, @NotNull Function<EVENT, AbstractEvent<?>> pAnswerCreation);

    /**
     * Resets the whole content of this facade
     *
     * @return Builder
     */
    @NotNull IWriteable reset();
  }

  /**
   * Contains all READ methods for this facade
   */
  interface IReadable
  {

    /**
     * Returns the event which should be the answer for the given event
     *
     * @param pSourceEvent  Event, that has to be answered
     * @param pDefaultEvent Supplier to get a default event, if no other event was specified
     * @return the answer
     */
    @NotNull <T extends AbstractEvent<T>> Single<T> answerEvent(@NotNull AbstractEvent<?> pSourceEvent, @NotNull Supplier<T> pDefaultEvent);
  }

}
