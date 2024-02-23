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

    private final String createBuildConfigUrl = "/admin"
            + "/createObjectMenu.html?projectId=%s&showMode=createBuildTypeMenu";
    private SelenideElement discoveryWaitingMarker = element(Selectors.byId("discoveryProgress"));
    private SelenideElement confirmStepsButton = element(Selectors.byClass("btn btn_primary"));
    private SelenideElement settingsUpdatedMsg = element(Selectors.byId("unprocessed_buildRunnerSettingsUpdated"));

    /**
     * Переход на страницу
     * @return текущий экземпляр CreateBuildConfigurationPage
     */
    public CreateBuildConfigurationPage open(String projectId) {
        Selenide.open(String.format(createBuildConfigUrl, projectId));
        waitUntilPageIsLoaded();
        return this;
    }

    /**
     * Создание BuildConfiguration для репозитория
     * @param url - ссылка на репозиторий
     * @return текущий экземпляр CreateBuildConfigurationPage
     */
    public CreateBuildConfigurationPage createBuildConfigByUrl(String url) {
        urlInput.sendKeys(url);
        new SaveButtonsBlockElement().submit();
        return this;
    }

    /**
     * Настройка BuildConfiguration
     * @param buildTypeName - название билда
     * @return текущий экземпляр CreateBuildConfigurationPage
     */
    public CreateBuildConfigurationPage setupBuildConfig(String buildTypeName) {
        waitUntilElementVisible(buildTypeNameInput);
        buildTypeNameInput.clear();
        buildTypeNameInput.sendKeys(buildTypeName);
        new SaveButtonsBlockElement().submit();
        discoveryWaitingMarker.shouldNotBe(Condition.visible, Duration.ofMinutes(1));
        return this;
    }

    /**
     * Выбор шагов для BuildConfiguration
     * @return текущий экземпляр CreateBuildConfigurationPage
     */
    public CreateBuildConfigurationPage selectSteps() {
        new DiscoveredRunnersElement().selectAll();
        confirmStepsButton.click();
        settingsUpdatedMsg.shouldBe(Condition.visible, Duration.ofSeconds(30));
        return this;
    }
}
