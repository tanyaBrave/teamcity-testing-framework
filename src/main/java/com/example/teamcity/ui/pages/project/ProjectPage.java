package com.example.teamcity.ui.pages.project;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.elements.BuildElement;
import com.example.teamcity.ui.pages.Page;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.element;
import static com.codeborne.selenide.Selenide.elements;

public class ProjectPage extends Page {

    private static final String PROJECT_URL = "/project/%s";

    private ElementsCollection builds = elements(Selectors.byClass("BuildTypeLine__buildTypeInfo--Zh"));
    private SelenideElement ringTab = element(Selectors.byDataTest("ring-tab"));

    /**
     * Переход на страницу
     * @return текущий экземпляр ProjectPage
     */
    public ProjectPage open(String projectId) {
        Selenide.open(String.format(PROJECT_URL, projectId));
        waitUntilPageIsLoaded();
        ringTab.shouldBe(Condition.visible, Duration.ofSeconds(30));
        return this;
    }

    /**
     * @return коллекцию найденных BuildElement на странице проектов
     */
    public List<BuildElement> getBuild() {
        return generatePageElements(builds, BuildElement::new);
    }
}
