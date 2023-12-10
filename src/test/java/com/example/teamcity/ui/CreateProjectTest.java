package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.ui.pages.admin.CreateNewProjectPage;
import com.example.teamcity.ui.pages.favorites.FavouriteProjectsPage;
import org.testng.annotations.Test;

public class CreateProjectTest extends BaseUiTest {

    @Test
    public void authorizedUserShouldBeAbleToCreateNewProject() {
        var testData = testDataStorage.addTestData();

        loginAsUser(testData.getUser());

        new CreateNewProjectPage()
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectByUrl(GIT_REPOSITORY_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        new FavouriteProjectsPage()
                .open()
                .getSubprojects()
                .stream().reduce((first, second) -> second).get()
                .getHeader().shouldHave(Condition.text(testData.getProject().getName()));

        checkProjectIsCreated(getCheckedRequestForUser(testData.getUser()),
                "/name:" + testData.getProject().getName());
    }

    @Test
    public void creatingTwoProjectsWithSameNameShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        loginAsUser(testData.getUser());

        new CreateNewProjectPage()
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectByUrl(GIT_REPOSITORY_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        new CreateNewProjectPage()
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectByUrl(GIT_REPOSITORY_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName())
                .checkErrorText("projectName", String.format(Errors.PROJECT_WITH_NAME_ALREADY_EXISTS.getText(),
                        testData.getProject().getName()));

        var projects = getCheckedRequestForUser(testData.getUser()).getProjectRequest()
                .get("/id:" + testData.getProject().getParentProject().getLocator())
                .getProjects().getProject()
                .stream().filter(project -> project.getName().equals(testData.getProject().getName()));

        softy.assertThat(projects).hasSize(1);
    }
}
