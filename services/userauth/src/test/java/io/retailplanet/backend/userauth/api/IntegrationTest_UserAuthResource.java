package io.retailplanet.backend.userauth.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * @author w.glanzer, 11.09.2019
 */
@QuarkusTest
public class IntegrationTest_UserAuthResource
{

  @Test
  void test_validate()
  {
    // @formatter:off
    given()
        .queryParam("clientID", "user1")
        .queryParam("scope", "myScope")
        .when().get("/internal/userauth")
        .then()
          .statusCode(200);
    // @formatter:on
  }

}
