package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.models.response.ProjectResponse;
import com.example.teamcity.ui.pages.admin.CreateNewProjectPage;
import com.example.teamcity.ui.pages.favorites.FavouriteProjectsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.qameta.allure.testng.Tag;
import org.testng.annotations.Test;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.MINOR;

@Epic("UI tests")
@Feature("TeamCity project")
@Story("Creating project")
@Tag("Regression")
public class CreateProjectTest extends BaseUiTest {

    @Test(groups = {"Regression"})
    @Description("Authorized user should be able to create new project")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-46")
    public void authorizedUserShouldBeAbleToCreateNewProject() {
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

        checkProjectIsCreated(getSpecForUser(testData.getUser()),
                "/name:" + testData.getProject().getName());
    }

    @Test(groups = {"Regression"})
    @Description("Creating two projects with same name should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-47")
    public void creatingTwoProjectsWithSameNameShouldNotBeAvailable() {
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

        var project = (ProjectResponse) getCheckedRequestForUser(testData.getUser()).getRequest(Endpoint.PROJECTS)
                .get("/id:" + testData.getProject().getParentProject().getLocator());
        var actualProjects = project.getProjects().getProject()
                .stream().filter(p -> p.getName().equals(testData.getProject().getName()));

        softy.assertThat(actualProjects).hasSize(1);
    }
}
