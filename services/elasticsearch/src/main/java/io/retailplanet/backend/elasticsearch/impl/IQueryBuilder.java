package io.retailplanet.backend.elasticsearch.impl;

import org.elasticsearch.index.query.QueryBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * @author w.glanzer, 16.07.2019
 */
public interface IQueryBuilder
{

  /**
   * @return Generates an elasticsearch querybuilder part for this object
   */
  @Nullable
  QueryBuilder toQueryBuilder();

}
