package com.example.teamcity.setup;

import com.example.teamcity.api.BaseApiTest;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.response.Agent;
import com.example.teamcity.api.models.response.AgentResponse;
import org.awaitility.Awaitility;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AgentSetupTest extends BaseApiTest {

    @Test
    public void authorizeAgentTest() {
        var agents = waitUntilAgentAppeared();
        authorizeAgent("true", "/name:" + agents.get(0).getName());
    }

    private List<Agent> waitUntilAgentAppeared() {
        var checkedRequest = checkedWithSuperUser.getRequest(Endpoint.AGENT);
        var atomicAgentResponse = new AtomicReference<>(new AgentResponse());
        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    atomicAgentResponse.set((AgentResponse) checkedRequest.get("?locator=enabled:true,authorized:false"));
                    return !atomicAgentResponse.get().getAgent().isEmpty();

                });
        return atomicAgentResponse.get().getAgent();
    }
}
