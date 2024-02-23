package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.pages.Page;

import static com.codeborne.selenide.Selenide.element;

public class CreateNewProjectPage extends Page {

    private final String createProjectUrl = "/admin/createObjectMenu.html?projectId=%s&showMode=createProjectMenu";
    private SelenideElement projectNameInput = element(Selectors.byId("projectName"));

    /**
     * Переход на страницу
     * @param parentProjectId - проект, в котром будет создан новый проект
     * @return текущий экземпляр CreateNewProjectPage
     */
    public CreateNewProjectPage open(String parentProjectId) {
        Selenide.open(String.format(createProjectUrl, parentProjectId));
        waitUntilPageIsLoaded();
        return this;
    }

    /**
     * Создание проекта
     * @param url - ссылка на репозиторий
     * @return текущий экземпляр CreateNewProjectPage
     */
    public CreateNewProjectPage createProjectByUrl(String url) {
        urlInput.sendKeys(url);
        submit();
        return this;
    }

    /**
     * Настройка проекта
     * @param projectName - развание проекта
     * @param buildTypeName - название buildType
     * @return
     */
    public CreateNewProjectPage setupProject(String projectName, String buildTypeName) {
        waitUntilElementVisible(projectNameInput);
        projectNameInput.clear();
        projectNameInput.sendKeys(projectName);
        buildTypeNameInput.clear();
        buildTypeNameInput.sendKeys(buildTypeName);
        submit();
        return this;
    }
}
