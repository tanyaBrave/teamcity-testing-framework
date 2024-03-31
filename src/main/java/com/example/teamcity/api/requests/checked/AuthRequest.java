package com.example.teamcity.api.requests.checked;

import com.example.teamcity.api.models.User;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;

public class AuthRequest {

    private User user;

    public AuthRequest(User user) {
        this.user = user;
    }

    /**
     * @return возвращает строку с csrf токеном
     */
    public String getCsrfToken() {
        return RestAssured
                .given()
                .spec(Specifications.getSpec().authSpec(user))
                .get("/authenticationTest.html?csrf")
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().asString();
    }
}
