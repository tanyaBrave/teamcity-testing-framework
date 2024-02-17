package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.ui.pages.admin.CreateBuildConfigurationPage;
import com.example.teamcity.ui.pages.project.ProjectPage;
import com.github.javafaker.Faker;
import org.testng.annotations.Test;

public class CreateBuildConfigurationTest extends BaseUiTest {

    @Test(groups = {"Regression"})
    public void creatingBuildConfigurationWithStepsShouldBeAvailable() {
       loginAsUser(testData.getUser());

        createProject(testData);

        new CreateBuildConfigurationPage()
                .open(testData.getProject().getId())
                .createBuildConfigByUrl(GIT_REPOSITORY_URL)
                .setupBuildConfig(testData.getBuildType().getName())
                .selectSteps();

        new ProjectPage()
                .open(testData.getProject().getId())
                .getBuild()
                .stream().reduce((first, second) -> second).get()
                .getTitle().shouldHave(Condition.text(testData.getBuildType().getName()));

        checkBuildConfigIsCreated(getCheckedRequestForUser(testData.getUser()),
                "/name:" + testData.getBuildType().getName());
    }

    @Test(groups = {"Regression"})
    public void creatingBuildConfigurationWithNotExistedRepoShouldNotBeAvailable() {
        loginAsUser(testData.getUser());

        createProject(testData);

        new CreateBuildConfigurationPage()
                .open(testData.getProject().getId())
                .createBuildConfigByUrl(new Faker().internet().url())
                .checkErrorText("url", Errors.UNRECOGNIZED_URL.getText());

        checkBuildConfigIsNotCreated(getCheckedRequestForUser(testData.getUser()),
                "/name:" + testData.getProject().getName());
    }
}
