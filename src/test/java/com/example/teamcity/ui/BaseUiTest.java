package com.example.teamcity.ui;

import com.codeborne.selenide.Configuration;
import com.example.teamcity.BaseTest;
import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.LoginPage;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeSuite;

public class BaseUiTest extends BaseTest {

    protected final static String GIT_REPOSITORY_URL = "https://github.com/tanyaBrave/teamcity-testing-framework";

    @BeforeSuite
    @Step("Setup UI tests configuration")
    public void setUiTests() {
        Configuration.baseUrl = "http://" + Config.getProperty("host");
        Configuration.remote = Config.getProperty("remote");
        Configuration.reportsFolder = "target/surefire-reports";
        Configuration.downloadsFolder = "target/downloads";

        BrowserSettings.setup(Config.getProperty("browser"));
    }

    @Step("Create user and login as this user")
    public void loginAsUser(User user) {
        new CheckedRequests(Specifications.getSpec().superUserSpec())
                .getRequest(Endpoint.USERS)
                .create(user);

        new LoginPage().open().login(user);
    }

}
