package io.retailplanet.backend.common.util;

import io.retailplanet.backend.common.objects.index.*;
import org.junit.jupiter.api.*;

import javax.json.bind.JsonbBuilder;

/**
 * @author w.glanzer, 08.09.2019
 */
public class Test_Jsonify
{

  @Test
  void testQuery()
  {
    Query query = new Query()
        .matches(Match.equal("test", "test2", Match.Operator.AND));
    String json = JsonbBuilder.create().toJson(query);
    Assertions.assertNotEquals("{}", json);
  }

  @Test
  void testSearchResult()
  {
    String json = JsonbBuilder.create().toJson(new SearchResult().count(2));
    Assertions.assertNotEquals("{}", json);
  }

}
