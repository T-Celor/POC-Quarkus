package org.tcelor.quarkus.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.tcelor.quarkus.api.entity.Person;
import org.tcelor.quarkus.api.resource.user.PersonResource;

import jakarta.inject.Inject;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@QuarkusTest
public class PersonResourceTest {

    @Inject
    PersonResource personResource;

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void testGetPersons() {
        given()
            .when().get("/api/persons")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].firstname", equalToIgnoringCase("John"));
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void testGetSinglePerson() {
        // Assuming you have a person with id 1 in your test data
        given()
            .pathParam("id", 1)
            .when().get("/api/persons/{id}")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("firstname", equalToIgnoringCase("John"));
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void testCreatePerson() {
        Person newPerson = new Person();
        newPerson.firstname = "John";
        newPerson.lastname = "Doe";

        given()
            .contentType(ContentType.JSON)
            .body(newPerson)
            .when().post("/api/persons")
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("firstname", equalToIgnoringCase("John"))
            .body("lastname", equalToIgnoringCase("Doe"));
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void testDeletePerson() {
        given()
            .pathParam("id", 1)
            .when().delete("/api/persons/{id}")
            .then()
            .statusCode(200);
    }
}
