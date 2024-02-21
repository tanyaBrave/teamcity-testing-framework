package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.ui.pages.admin.CreateBuildConfigurationPage;
import com.example.teamcity.ui.pages.project.ProjectPage;
import com.github.javafaker.Faker;
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
@Feature("TeamCity build configuration")
@Story("Creating build configuration")
@Tag("Regression")
public class CreateBuildConfigurationTest extends BaseUiTest {

    @Test(groups = {"Regression"})
    @Description("Creating build configuration with steps should be available")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-48")
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
    @Description("Creating build configuration with non existed repository should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-49")
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
