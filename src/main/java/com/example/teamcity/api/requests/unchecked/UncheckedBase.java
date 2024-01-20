package com.example.teamcity.api.requests.unchecked;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UncheckedBase extends Request implements CrudInterface {
    public UncheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
    }

    @Override
    public Response create(Object obj) {
        return given()
                .spec(spec)
                .body(obj)
                .post(endpoint.getUrl());
    }

    @Override
    public Response get(String locator) {
        return given()
                .spec(spec)
                .get(endpoint.getUrl() + locator);
    }

    @Override
    public Response update(Object obj, String locator) {
        return given()
                .spec(spec)
                .body(obj)
                .put(endpoint.getUrl() + locator);
    }

    @Override
    public Response delete(String locator) {
        return given()
                .spec(spec)
                .delete(endpoint.getUrl() + locator);
    }
}
