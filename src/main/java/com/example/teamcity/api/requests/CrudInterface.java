package com.example.teamcity.api.requests;

public interface CrudInterface {
    public Object create(Object obj);

    public Object get(String id);

    public Object update(Object obj, String id);

    public Object delete(String id);
}
