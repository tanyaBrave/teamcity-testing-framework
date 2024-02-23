package com.example.teamcity.api.requests.checked;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

public class CheckedBase extends Request implements CrudInterface {
    public CheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
    }

    /**
     * @param obj - тело запроса
     * @return проверенный на 200 код ответ, извлеченный как BaseModel.class
     */
    @Override
    public BaseModel create(Object obj) {
        var model = new UncheckedBase(spec, endpoint)
                .create(obj)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
        TestDataStorage.getStorage().addCreatedEntity(endpoint, model.getId());
        return model;
    }

    /**
     * @param locator - TeamCity локатор для поиска созданной сущности
     * @return проверенный на 200 код ответ, извлеченный как BaseModel.class
     */
    @Override
    public BaseModel get(String locator) {
        return new UncheckedBase(spec, endpoint)
                .get(locator)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
    }

    /**
     * @param obj - тело запроса
     * @param locator - TeamCity локатор для поиска созданной сущности
     * @return проверенный на 200 код ответ
     */
    @Override
    public Object update(Object obj, String locator) {
        return new UncheckedBase(spec, endpoint)
                .update(obj, locator)
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    /**
     * @param locator - TeamCity локатор для поиска созданной сущности
     * @return проверенный на 204 код ответ, извлеченный как String.class
     */
    @Override
    public String delete(String locator) {
        return new UncheckedBase(spec, endpoint)
                .delete(locator)
                .then().assertThat().statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().asString();
    }
}
