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

    /**
     * @param obj - тело запроса
     * @return ответ от POST запроса на сущности
     */
    @Override
    public Response create(Object obj) {
        return given()
                .spec(spec)
                .body(obj)
                .post(endpoint.getUrl());
    }

    /**
     *
     * @param locator - TeamCity локатор для поиска созданной сущности
     * @return ответ от GET запроса на поиск сущности
     */
    @Override
    public Response get(String locator) {
        return given()
                .spec(spec)
                .get(endpoint.getUrl() + locator);
    }

    /**
     * @param obj - тело запроса
     * @param locator - TeamCity локатор для поиска созданной сущности
     * @return ответ от GET запроса на обновление сущности
     */
    @Override
    public Response update(Object obj, String locator) {
        return given()
                .spec(spec)
                .body(obj)
                .put(endpoint.getUrl() + locator);
    }

    /**
     * @param locator - TeamCity локатор для поиска созданной сущности
     * @return ответ от DELETE запроса на удаление сущности
     */
    @Override
    public Response delete(String locator) {
        return given()
                .spec(spec)
                .delete(endpoint.getUrl() + locator);
    }
}
