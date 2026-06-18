package com.epam.learn.e2e.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.io.IOException;
import java.io.InputStream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceUploadSteps {

    private static final String GATEWAY_URL =
        System.getenv().getOrDefault("GATEWAY_URL", "http://localhost:8080");

    private byte[] mp3File;
    private int resourceId;

    @Given("user has a valid mp3 file")
    public void userHasValidMp3File() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("audio/sample.mp3")) {
            assertNotNull(inputStream);
            mp3File = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @When("user uploads the mp3 file")
    public void userUploadsMp3File() {
        Response response = given()
            .baseUri(GATEWAY_URL)
            .contentType("audio/mpeg")
            .body(mp3File)
        .when()
            .post("/resources")
        .then()
            .statusCode(200)
            .extract()
            .response();

        resourceId = response.jsonPath().getInt("id");
    }

    @Then("resource should be available")
    public void resourceShouldBeAvailable() {
        Response response = given()
            .baseUri(GATEWAY_URL)
        .when()
            .get("/resources/{id}", resourceId)
        .then()
            .statusCode(200)
            .extract()
            .response();

        assertArrayEquals(mp3File, response.asByteArray());
    }

    @And("song metadata should be available")
    public void songMetadataShouldBeAvailable() {
        Response response = given()
            .baseUri(GATEWAY_URL)
        .when()
            .get("/songs/{id}", resourceId)
        .then()
            .statusCode(200)
            .extract()
            .response();

        assertEquals(resourceId, response.jsonPath().getInt("id"));
        assertNotNull(response.jsonPath().getString("name"));
        assertNotNull(response.jsonPath().getString("artist"));
        assertNotNull(response.jsonPath().getString("album"));
        assertNotNull(response.jsonPath().getString("duration"));
        assertNotNull(response.jsonPath().getString("year"));
    }

}
