package in.reqres.tests;

import in.reqres.models.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static in.reqres.helpers.CustomAllureListener.withCustomTemplates;
import static in.reqres.specs.LoginSpec.loginRequestSpec;
import static in.reqres.specs.LoginSpec.loginResponseSpec;
import static in.reqres.specs.RegisterSpec.registerRequestSpec;
import static in.reqres.specs.RegisterSpec.registerResponseSpec;
import static in.reqres.specs.UpdateSpec.updateRequestSpec;
import static in.reqres.specs.UpdateSpec.updateResponseSpec;
import static in.reqres.specs.UserSpec.userRequestSpec;
import static in.reqres.specs.UserSpec.userResponseSpec;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReqresInTests extends TestBase{

    @DisplayName("LOGIN-SUCCESSFUL")
    @Test
    void successfulLoginTest() {
        LoginBodyLombokModel authData = new LoginBodyLombokModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");

        LoginResponseLombokModel response = step("Make login request", () ->
                given(loginRequestSpec)
                .body(authData)
                .when()
                .post("/login")
                .then()
                .spec(loginResponseSpec)
                .extract().as(LoginResponseLombokModel.class));

        step("verify response", () ->
        assertEquals("QpwL5tke4Pnpja7X4",response.getToken()));
    }

    @DisplayName("LIST USERS")
    @Test
    void getListUsers() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("/users?page=2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("page",is(2))
                .body("per_page",is(6))
                .body("total_pages",is(2))
                .body("total",is(12));
    }

    @DisplayName("UPDATE")
    @Test
    void putUpdateTest() {
        UpdateBodyLombokModel authData = new UpdateBodyLombokModel();
        authData.setName("morpheus");
        authData.setName("zion resident");

        UpdateResponseLombokModel response = step("Make update request", () ->
        given(updateRequestSpec)
                .body(authData)
                .when()
                .put("/users/2")
                .then()
                .spec(updateResponseSpec)
                .extract().as(UpdateResponseLombokModel.class));

        step("Verify Results", () -> {
            assertEquals("morpheus", response.getName());
            assertEquals("zion resident", response.getJob());
        });
    }

    @DisplayName("CREATE") // №1
    @Test
    void createUser(){

        UserBodyModel authDate = new UserBodyModel();
        authDate.setName("morpheus");
        authDate.setJob("leader");

        UserResponseModel response = step("Make user request", () ->
        given()
                .filter(withCustomTemplates())
                .spec(userRequestSpec)
                .when()
                .get("/users?page=2")
                .then()
                .spec(userResponseSpec)
                .extract().as(UserResponseModel.class));

        step("Verify", () -> {
            assertEquals("morpheus", response.getName());
            assertEquals("leader", response.getJob());
            assertEquals(60, response.getId());
            assertEquals("2023-10-03T18:48:28.915Z", response.getTime());
        });

    }

    @DisplayName("REGISTER-UNSUCCESSFUL")
    @Test
    void successfulRegister(){
        RegisterBodyLombokModel authData = new RegisterBodyLombokModel();
        authData.setEmail("sydney@fife");

        RegisterResponseLombokModel response = step("Make request", () ->
        given(registerRequestSpec)
                .body(authData)
                .when()
                .post("/register")
                .then()
                .spec(registerResponseSpec)
                .extract().as(RegisterResponseLombokModel.class));

        assertEquals("Missing email or username", response.getError());
    }
}
