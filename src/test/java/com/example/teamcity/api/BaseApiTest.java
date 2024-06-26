package com.example.teamcity.api;

import com.example.teamcity.BaseTest;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.generators.TestData;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.NewProjectDescription;
import com.example.teamcity.api.models.Step;
import com.example.teamcity.api.models.response.ParentProject;
import com.example.teamcity.api.models.response.ProjectResponse;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class BaseApiTest extends BaseTest {

    @io.qameta.allure.Step("Set archive value to {0} for project")
    protected void archiveProject(String needArchive, String locator) {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.TEXT);
        spec.setAccept(ContentType.TEXT);
        new CheckedRequests(spec.build()).getRequest(Endpoint.PROJECTS).update(needArchive, locator + "/archived");
    }

    @io.qameta.allure.Step("Create build configuration")
    protected BuildType createBuildConfig(RequestSpecification spec, BuildType buildType) {
        return (BuildType) new CheckedRequests(spec)
                .getRequest(Endpoint.BUILD_TYPES)
                .create(buildType);
    }

    @io.qameta.allure.Step("Set auth value to {0} for agent")
    protected void authorizeAgent(String needAuth, String locator) {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.TEXT);
        spec.setAccept(ContentType.TEXT);
        new CheckedRequests(spec.build()).getRequest(Endpoint.AGENT).update(needAuth, locator + "/authorized");
    }

    @io.qameta.allure.Step("Check that project with {1} = {2} was not created")
    protected void checkProjectIsNotCreated(UncheckedRequests request, String locator, String value) {
        var response = request.getRequest(Endpoint.PROJECTS)
                .get("/" + locator + ":" + value)
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND);
        switch (locator) {
            case ("id"):
                response.body(Matchers.containsString(String.format(Errors.PROJECT_NOT_FOUND_BY_ID.getText(), value)));
                break;
            case ("name"):
                response.body(Matchers.containsString(String.format(Errors.NOTHING_FOUND_BY_NAME.getText(), value)));
                break;
        }
    }

    @io.qameta.allure.Step("Check that created project data is correct")
    protected void checkCreatedProjectData(ProjectResponse actualProject, NewProjectDescription expectedProject,
                                           String parentProjectId) {
        softy.assertThat(actualProject).hasNoNullFieldsOrProperties();
        softy.assertThat(actualProject).extracting(ProjectResponse::getId, ProjectResponse::getName,
                        ProjectResponse::getParentProjectId)
                .containsExactly(expectedProject.getId(), expectedProject.getName(), parentProjectId);
    }

    @io.qameta.allure.Step("Check that parent project data is correct")
    protected void checkParentProject(ParentProject actualParentProject, String expectedParentId,
                                      String expectedParentName) {
        if(expectedParentId.equals("_Root")) {
            softy.assertThat(actualParentProject).extracting(ParentProject::getId, ParentProject::getName,
                            ParentProject::getDescription)
                    .containsExactly(expectedParentId, "<Root project>", "Contains all other projects");
        } else {
            softy.assertThat(actualParentProject).extracting(ParentProject::getId, ParentProject::getName)
                    .containsExactly(expectedParentId, expectedParentName);
        }
    }

    @io.qameta.allure.Step("Check that build configuration with {1} = {2} was not created")
    protected void checkBuildConfigIsNotCreated(UncheckedRequests request, String locator, String value) {
        var response = request.getRequest(Endpoint.BUILD_TYPES)
                .get("/" + locator + ":" + value)
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND);
        switch (locator) {
            case ("id"):
                response.body(Matchers.containsString(String.format(Errors.BUILD_CONFIG_NOT_FOUND_BY_ID.getText(), value)));
                break;
            case ("name"):
                response.body(Matchers.containsString(String.format(Errors.NOTHING_FOUND_BY_NAME.getText(), value)));
                break;
        }
    }

    @io.qameta.allure.Step("Check that created build configuration data is correct")
    protected void checkCreatedBuildConfigData(BuildType actualBuildConfig, TestData expectedData) {
        softy.assertThat(actualBuildConfig).hasNoNullFieldsOrProperties();
        var expectedBuildConfig = expectedData.getBuildType();
        var project = expectedData.getProject();
        softy.assertThat(actualBuildConfig).extracting(BuildType::getId, BuildType::getName,
                        BuildType::getProjectId, BuildType::getProjectName)
                .containsExactly(expectedBuildConfig.getId(), expectedBuildConfig.getName(),
                        project.getId(), project.getName());
    }

    @io.qameta.allure.Step("Check that created build configuration step data is correct")
    protected void checkCreatedBuildConfigStepData(Step actualSteps, Step expectedSteps) {
        softy.assertThat(actualSteps).usingRecursiveComparison()
                .isEqualTo(expectedSteps);
    }
}
