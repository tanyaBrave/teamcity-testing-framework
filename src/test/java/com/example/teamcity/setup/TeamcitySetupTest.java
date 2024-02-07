package com.example.teamcity.setup;

import com.codeborne.selenide.Condition;
import com.example.teamcity.ui.BaseUiTest;
import com.example.teamcity.ui.pages.StartUpPage;
import org.testng.annotations.Test;

public class TeamcitySetupTest extends BaseUiTest {

    @Test
    public void startUpTest() {
        new StartUpPage()
                .open()
                .startUpTeamcityServer()
                .getHeader().shouldHave(Condition.text("Create Administrator Account"));
    }
}
