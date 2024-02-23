package com.example.teamcity.api.requests;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import java.util.EnumMap;

@Getter
public class UncheckedRequests {

    private final EnumMap<Endpoint, UncheckedBase> uncheckedRequests = new EnumMap<>(Endpoint.class);

    public UncheckedRequests(RequestSpecification spec) {
        for (var endpoint : Endpoint.values()) {
            uncheckedRequests.put(endpoint, new UncheckedBase(spec, endpoint));
        }
    }

    /**
     * @param endpoint - вызываемый эндпоинт
     * @return созданный unchecked-request для переданного эндпоинта
     */
    public UncheckedBase getRequest(Endpoint endpoint) {
        return uncheckedRequests.get(endpoint);
    }
}
