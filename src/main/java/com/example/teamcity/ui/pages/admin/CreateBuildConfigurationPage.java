package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.elements.DiscoveredRunnersElement;
import com.example.teamcity.ui.elements.SaveButtonsBlockElement;
import com.example.teamcity.ui.pages.Page;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.element;

public class CreateBuildConfigurationPage extends Page {

    private final String CREATE_BUILD_CONFIG_URL = "/admin/createObjectMenu.html?projectId=%s&showMode=createBuildTypeMenu";
    private SelenideElement discoveryWaitingMarker = element(Selectors.byId("discoveryProgress"));
    private SelenideElement confirmStepsButton = element(Selectors.byClass("btn btn_primary"));
    private SelenideElement settingsUpdatedMsg = element(Selectors.byId("unprocessed_buildRunnerSettingsUpdated"));

    public CreateBuildConfigurationPage open(String projectId) {
        Selenide.open(String.format(CREATE_BUILD_CONFIG_URL, projectId));
        waitUntilPageIsLoaded();
        return this;
    }

    public CreateBuildConfigurationPage createBuildConfigByUrl(String url) {
        urlInput.sendKeys(url);
        new SaveButtonsBlockElement().submit();
        return this;
    }

    public CreateBuildConfigurationPage setupBuildConfig(String buildTypeName) {
        waitUntilElementVisible(buildTypeNameInput);
        buildTypeNameInput.clear();
        buildTypeNameInput.sendKeys(buildTypeName);
        new SaveButtonsBlockElement().submit();
        discoveryWaitingMarker.shouldNotBe(Condition.visible, Duration.ofMinutes(1));
        return this;
    }

    public CreateBuildConfigurationPage selectSteps() {
        new DiscoveredRunnersElement().selectAll();
        confirmStepsButton.click();
        settingsUpdatedMsg.shouldBe(Condition.visible, Duration.ofSeconds(30));
        return this;
    }
}
