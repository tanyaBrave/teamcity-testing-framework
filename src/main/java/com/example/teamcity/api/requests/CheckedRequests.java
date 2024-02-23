package com.example.teamcity.api.requests;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.requests.checked.CheckedBase;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import java.util.EnumMap;

@Getter
public class CheckedRequests {

    private final EnumMap<Endpoint, CheckedBase> checkedRequests = new EnumMap<>(Endpoint.class);

    public CheckedRequests(RequestSpecification spec) {
        for (var endpoint : Endpoint.values()) {
            checkedRequests.put(endpoint, new CheckedBase(spec, endpoint));
        }
    }

    /**
     * @param endpoint - вызываемый эндпоинт
     * @return созданный checked-request для переданного эндпоинта
     */
    public CheckedBase getRequest(Endpoint endpoint) {
        return checkedRequests.get(endpoint);
    }
}
