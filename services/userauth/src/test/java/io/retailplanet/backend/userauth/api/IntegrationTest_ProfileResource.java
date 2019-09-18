package io.retailplanet.backend.userauth.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * @author w.glanzer, 11.09.2019
 */
@QuarkusTest
public class IntegrationTest_ProfileResource
{

  @Test
  void test_information()
  {
    // @formatter:off
    given()
        .header("Authorization", UUID.randomUUID().toString())
        .when().get("/profile")
        .then()
          .statusCode(200)
          .contentType(MediaType.APPLICATION_JSON)
          .body("id", notNullValue())
          .body("roles", notNullValue());
    // @formatter:on
  }

  @Test
  void test_information_without_authorization()
  {
    // @formatter:off
    given()
        .when().get("/profile")
        .then()
          .statusCode(400);
    // @formatter:on
  }

  @Disabled
  @Test
  void test_information_invalid_token()
  {
    // @formatter:off
    given()
        .header("Authorization", "IAMINVALID")
        .when().get("/profile")
        .then()
          .statusCode(400);
    // @formatter:on
  }

  @Test
  void test_avatar()
  {
    // @formatter:off
    given()
        .pathParam("userid", "user1")
        .when().get("/profile/avatar/{userid}.png")
        .then()
          .statusCode(200)
          .contentType("image/png")
          .body(equalTo("BASE64:user1_AVATAR_PLACEHOLDER"));
    // @formatter:on
  }

  @Test
  void test_avatar_undefinedUser()
  {
    // @formatter:off
    given()
        .pathParam("userid", "userMustNotBeFoundInDirectory")
        .when().get("/profile/avatar/{userid}.png")
        .then()
          .statusCode(404);
    // @formatter:on
  }

}
