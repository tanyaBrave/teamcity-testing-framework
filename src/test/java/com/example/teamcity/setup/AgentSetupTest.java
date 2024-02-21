package com.example.teamcity.setup;

import com.example.teamcity.api.BaseApiTest;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.response.Agent;
import com.example.teamcity.api.models.response.AgentResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.awaitility.Awaitility;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.qameta.allure.SeverityLevel.BLOCKER;

@Epic("Setup infra tests")
@Feature("TeamCity agent")
@Story("Authorize agent")
public class AgentSetupTest extends BaseApiTest {

    @Test
    @Description("Authorization agent after setup infra")
    @Severity(BLOCKER)
    @TmsLink("TC-TMS-50")
    public void authorizeAgentTest() {
        var agents = waitUntilAgentAppeared();
        authorizeAgent("true", "/name:" + agents.get(0).getName());
    }

    @Step("Waiting until agent will be available")
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
