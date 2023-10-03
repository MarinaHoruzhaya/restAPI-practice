package in.reqres.tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ReqresInTests extends TestBase {

    @DisplayName("LOGIN-SUCCESSFUL")
    @Test
    void successfulLoginTest() {
        String authData = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\"}";

        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(authData)
                .when()
                .post("/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("token",is("QpwL5tke4Pnpja7X4"));
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
        String authData = "{  \"name\": \"morpheus\", \"job\": \"zion resident\"}";

        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(authData)
                .when()
                .put("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name",is("morpheus"))
                .body("job",is("zion resident"));
    }

    @DisplayName("SINGLE USER NOT FOUND")
    @Test
    void singleUserNotFound(){
        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("/users/23")
                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @DisplayName("REGISTER-UNSUCCESSFUL")
    @Test
    void successfulRegister(){
        String authData = "{\"email\": \"sydney@fife\"}";
        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(authData)
                .when()
                .post("/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }


}
