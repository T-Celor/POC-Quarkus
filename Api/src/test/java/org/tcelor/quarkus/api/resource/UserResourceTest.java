package org.tcelor.quarkus.api.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.tcelor.quarkus.api.resource.user.UserResource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;


@QuarkusTest
public class UserResourceTest {

    @Inject
    UserResource userResource;

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void shouldNotAccessUserWhenAdminAuthenticated() {
        given()
                .when()
                .get("/api/users/me")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void shouldAccessUserAndGetIdentityWhenUserAuthenticated() {
        given()
                .when()
                .get("/api/users/me")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("{\"message\":\"Hello user\"}"));
    }
}
