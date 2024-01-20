package com.example.teamcity.api.requests;

public interface CrudInterface {
    Object create(Object obj);

    Object get(String locator);

    Object update(Object obj, String id);

    Object delete(String locator);
}
