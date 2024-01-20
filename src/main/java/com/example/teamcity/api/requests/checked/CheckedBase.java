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

    @Override
    public BaseModel create(Object obj) {
        var model = new UncheckedBase(spec, endpoint)
                .create(obj)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
        TestDataStorage.getStorage().addCreatedEntity(endpoint, model.getId());
        return model;
    }

    @Override
    public BaseModel get(String locator) {
        return new UncheckedBase(spec, endpoint)
                .get(locator)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());
    }

    @Override
    public Object update(Object obj, String id) {
        return new UncheckedBase(spec, endpoint)
                .update(obj, id)
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Override
    public String delete(String locator) {
        return new UncheckedBase(spec, endpoint)
                .delete(locator)
                .then().assertThat().statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().asString();
    }
}
