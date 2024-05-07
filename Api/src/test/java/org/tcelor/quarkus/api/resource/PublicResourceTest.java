package org.tcelor.quarkus.api.resource;

import static io.restassured.RestAssured.get;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.tcelor.quarkus.api.resource.all.PublicResource;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PublicResourceTest {

    @Inject
    PublicResource publicResource;

    @Test
    void shouldAccessPublicWhenAnonymous() {
        get("/api/public")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
