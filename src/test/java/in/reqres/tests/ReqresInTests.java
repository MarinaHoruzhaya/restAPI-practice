package in.reqres.tests;

import in.reqres.models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static in.reqres.specs.ListUserSpec.listUserRequestSpec;
import static in.reqres.specs.ListUserSpec.listUserResponseSpec;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReqresInTests extends TestBase{

    @DisplayName("LOGIN-SUCCESSFUL")
    @Test
    void successfulLoginTest() {
        LoginBodyModel authData = new LoginBodyModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");

        LoginResponseModel response = step("Make login request", () ->
                given(loginRequestSpec)
                .body(authData)
                .when()
                .post("/login")
                .then()
                .spec(loginResponseSpec)
                .extract().as(LoginResponseModel.class));

        step("verify response", () ->
        assertEquals("QpwL5tke4Pnpja7X4",response.getToken()));
    }

    @DisplayName("LIST USERS")
    @Test
    void getListUsers() {

        ListUsersResponseModel response = step("Make list users request", () ->
                given(listUserRequestSpec)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .spec(listUserResponseSpec)
                        .extract().as(ListUsersResponseModel.class));

        step("Verify common results about page", () -> {
            assertEquals(2, response.getPage());
            assertEquals(6, response.getPerPage());
            assertEquals(12, response.getTotal());
            assertEquals(2, response.getTotalPages());
        });
        step("Verify information about the second user", () -> {
            List<ListUserDataResponseModel> data = response.getData();
            assertEquals(8, data.get(1).getId());
            assertEquals("lindsay.ferguson@reqres.in", data.get(1).getEmail());
            assertEquals("Lindsay", data.get(1).getFirstName());
            assertEquals("Ferguson", data.get(1).getLastName());
            assertEquals("https://reqres.in/img/faces/8-image.jpg", data.get(1).getAvatar());
        });
        step("Verify information about support", () -> {
            ListUsersSupportResponseModel support = response.getSupport();
            assertEquals("https://reqres.in/#support-heading", support.getUrl());
            assertEquals("To keep ReqRes free, contributions towards server costs are appreciated!", support.getText());
        });
    }

    @DisplayName("UPDATE")
    @Test
    void putUpdateTest() {
        UpdateBodyModel authData = new UpdateBodyModel();
        authData.setName("morpheus");
        authData.setJob("zion resident");

        UpdateResponseModel response = step("Make update request", () ->
        given(updateRequestSpec)
                .body(authData)
                .when()
                .put("/users/2")
                .then()
                .spec(updateResponseSpec)
                .extract().as(UpdateResponseModel.class));

        step("Verify Results", () -> {
            assertEquals("morpheus", response.getName());
            assertEquals("zion resident", response.getJob());
        });
    }

    @DisplayName("CREATE")
    @Test
    void createUser(){

        UserBodyModel authDate = new UserBodyModel();
        authDate.setName("morpheus");
        authDate.setJob("leader");

        UserResponseModel response = step("Make user request", () ->
        given(userRequestSpec)
                .body(authDate)
                .when()
                .post("/users")
                .then()
                .spec(userResponseSpec)
                .extract().as(UserResponseModel.class));

        step("Verify", () -> {
           assertNotNull(response.getId());
           assertNotNull(response.getCreatedAt());
        });

    }

    @DisplayName("REGISTER-UNSUCCESSFUL")
    @Test
    void successfulRegister(){
        RegisterBodyModel authData = new RegisterBodyModel();
        authData.setEmail("sydney@fife");

        RegisterResponseModel response = step("Make request", () ->
        given(registerRequestSpec)
                .body(authData)
                .when()
                .post("/register")
                .then()
                .spec(registerResponseSpec)
                .extract().as(RegisterResponseModel.class));

        assertEquals("Missing email or username", response.getError());
    }
}
