package org.tcelor.quarkus.api.resource;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.tcelor.quarkus.api.resource.admin.AdminResource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;

@QuarkusTest
public class AdminResourceTest {

    @Inject
    AdminResource adminResource;

    @Test
    void shouldNotAccessAdminWhenAnonymous() {
        get("/api/admin")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void shouldAccessAdminWhenAdminAuthenticated() {
        given()
                .when()
                .get("/api/admin")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
