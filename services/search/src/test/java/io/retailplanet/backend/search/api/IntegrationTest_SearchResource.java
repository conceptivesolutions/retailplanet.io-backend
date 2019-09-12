package io.retailplanet.backend.search.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author w.glanzer, 11.09.2019
 */
@QuarkusTest
public class IntegrationTest_SearchResource
{

  @Test
  void test_search_only_query()
  {
    // @formatter:off
    given()
        .queryParam("query", "nice query")
        .when().get("/search")
        .then()
          .statusCode(200)
          .contentType(MediaType.APPLICATION_JSON)
          .body("maxSize", equalTo(8000));
    // @formatter:on
  }

  @Test
  void test_search_only_query_with_authorization()
  {
    // @formatter:off
    given()
        .header("Authorization", "user1")
        .queryParam("query", "nice query")
        .when().get("/search")
        .then()
          .statusCode(200)
          .contentType(MediaType.APPLICATION_JSON)
          .body("maxSize", equalTo(8000));
    // @formatter:on
  }

  @Test
  @Disabled
  void test_search_only_query_with_invalid_authorization()
  {
    // @formatter:off
    given()
        .header("Authorization", "IMUSTBEINVALID")
        .queryParam("query", "nice query")
        .when().get("/search")
        .then()
          .statusCode(401);
    // @formatter:on
  }

}
