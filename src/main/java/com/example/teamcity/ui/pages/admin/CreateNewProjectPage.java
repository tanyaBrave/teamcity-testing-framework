package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.pages.Page;

import static com.codeborne.selenide.Selenide.element;

public class CreateNewProjectPage extends Page {

    private final String CREATE_PROJECT_URL = "/admin/createObjectMenu.html?projectId=%s&showMode=createProjectMenu";
    private SelenideElement projectNameInput = element(Selectors.byId("projectName"));

    public CreateNewProjectPage open(String parentProjectId) {
        Selenide.open(String.format(CREATE_PROJECT_URL, parentProjectId));
        waitUntilPageIsLoaded();
        return this;
    }

    public CreateNewProjectPage createProjectByUrl(String url) {
        urlInput.sendKeys(url);
        submit();
        return this;
    }

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
