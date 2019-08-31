package io.retailplanet.backend.common.processor;

/**
 * A EventContainer represents a class containing a bunch of events
 *
 * @author w.glanzer, 19.06.2019
 */
public @interface EventContainer
{

  /**
   * @return GroupID for all inner events
   */
  String groupID();

  /**
   * @return Key-Value-Pairs of default values for all event descriptions
   */
  String[] defaults() default {};

}
