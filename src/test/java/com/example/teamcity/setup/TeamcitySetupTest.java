package com.example.teamcity.setup;

import com.codeborne.selenide.Condition;
import com.example.teamcity.ui.BaseUiTest;
import com.example.teamcity.ui.pages.StartUpPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import static io.qameta.allure.SeverityLevel.BLOCKER;

@Epic("Setup infra tests")
@Feature("TeamCity server")
@Story("Setup TeamCity server")
public class TeamcitySetupTest extends BaseUiTest {

    @Test
    @Description("Startup TeamCity server and accept license")
    @Severity(BLOCKER)
    @TmsLink("TC-TMS-51")
    public void startUpTest() {
        new StartUpPage()
                .open()
                .startUpTeamcityServer()
                .getHeader().shouldHave(Condition.text("Create Administrator Account"));
    }
}
