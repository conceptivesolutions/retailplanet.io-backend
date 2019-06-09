package io.retailplanet.backend.businesstoken;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

/**
 * @author w.glanzer, 09.06.2019
 */
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTest
class Test_BusinessTokenRessource
{

  @Test
  void testTokenCreation()
  {
    Response response = RestAssured.when()
        .get("/business/token/generate?clientid={0}", "testclient1");

    // Test if response is not empty and does not contain whitespaces -> should be a validity check
    Assertions.assertFalse(response.getBody().asString().trim().isEmpty());
    Assertions.assertFalse(response.getBody().asString().contains(" "));

    // Validate clientid
    Response response2 = RestAssured.when()
        .get("/business/token/{0}", response.getBody().asString());
    Assertions.assertEquals("testclient1", response2.jsonPath().getString("client_id"));
  }

  @Test
  void testTokenInvalidation()
  {
    // Create token
    Response response = RestAssured.when()
        .get("/business/token/generate?clientid={0}", "testclient2");

    // Invalidate token
    RestAssured.when()
        .delete("/business/token/{0}", response.getBody().asString())
        .then().statusCode(200);

    // Token should be not readable, because invalid
    RestAssured.when()
        .get("/business/token/{0}", response.getBody().asString())
        .then().statusCode(410);

    // Try regenerate new one, they must not be the same
    RestAssured.when()
        .get("/business/token/generate?clientid={0}", "testclient2")
        .then().body(Matchers.not(response.getBody().asString()));
  }

}
