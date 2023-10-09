package in.reqres.tests;

import in.reqres.models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static in.reqres.specs.UserSpecs.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.*;

public class ReqresInTests extends TestBase{

    @DisplayName("LOGIN-SUCCESSFUL")
    @Test
    void successfulLoginTest() {
        LoginBodyModel authData = new LoginBodyModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");

        LoginResponseModel response = step("Make login request", () ->
                given(requestSpec)
                .body(authData)
                .when()
                .post("/login")
                .then()
                .spec(responseSpec)
                .extract().as(LoginResponseModel.class));

        step("verify response", () ->
                assertThat(response.getToken()).isEqualTo("QpwL5tke4Pnpja7X4"));
    }

    @DisplayName("LIST USERS")
    @Test
    void getListUsers() {

        ListUsersResponseModel response = step("Make list users request", () ->
                given(requestSpec)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .spec(responseSpec)
                        .extract().as(ListUsersResponseModel.class));

        step("Verify common results about page", () -> {
            assertThat(response.getPage()).isEqualTo(2);
            assertThat(response.getPerPage()).isEqualTo(6);
            assertThat(response.getTotal()).isEqualTo(12);
            assertThat(response.getTotalPages()).isEqualTo(2);
        });

        step("Verify information about the second user", () -> {
            List<ListUserDataResponseModel> data = response.getData();
            assertThat(data.get(1).getId()).isEqualTo(8);
            assertThat(data.get(1).getEmail()).isEqualTo("lindsay.ferguson@reqres.in");
            assertThat(data.get(1).getFirstName()).isEqualTo("Lindsay");
            assertThat(data.get(1).getLastName()).isEqualTo("Ferguson");
            assertThat(data.get(1).getAvatar()).isEqualTo("https://reqres.in/img/faces/8-image.jpg");
        });

        step("Verify information about support", () -> {
            ListUsersSupportResponseModel support = response.getSupport();
            assertThat(support.getUrl()).isEqualTo("https://reqres.in/#support-heading");
            assertThat(support.getText()).isEqualTo("To keep ReqRes free, contributions towards server costs are appreciated!");
        });
    }

    @DisplayName("UPDATE")
    @Test
    void putUpdateTest() {
        UserBodyModel authData = new UserBodyModel();
        authData.setName("morpheus");
        authData.setJob("zion resident");

        UpdateResponseModel response = step("Make update request", () ->
        given(requestSpec)
                .body(authData)
                .when()
                .put("/users/2")
                .then()
                .spec(responseSpec)
                .extract().as(UpdateResponseModel.class));

        step("Verify Results", () -> {
            assertThat(response.getName()).isEqualTo("morpheus");
            assertThat(response.getJob()).isEqualTo("zion resident");
        });
    }

    @DisplayName("CREATE")
    @Test
    void createUser(){

        UserBodyModel authDate = new UserBodyModel();
        authDate.setName("morpheus");
        authDate.setJob("leader");

        CreateResponseModel response = step("Make user request", () ->
        given(requestSpec)
                .body(authDate)
                .when()
                .post("/users")
                .then()
                .spec(createUserResponseSpec)
                .extract().as(CreateResponseModel.class));

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
        given(requestSpec)
                .body(authData)
                .when()
                .post("/register")
                .then()
                .spec(registerResponseSpec)
                .extract().as(RegisterResponseModel.class));

        step("Verify results", () ->
                assertThat(response.getError()).isEqualTo("Missing password"));
    }
}
