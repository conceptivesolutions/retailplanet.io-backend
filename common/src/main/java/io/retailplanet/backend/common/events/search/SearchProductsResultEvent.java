package io.retailplanet.backend.common.events.search;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;

/**
 * Event: Product search returned result
 *
 * @author w.glanzer, 12.07.2019
 */
@RegisterForReflection
public class SearchProductsResultEvent extends AbstractEvent<SearchProductsResultEvent>
{
}
