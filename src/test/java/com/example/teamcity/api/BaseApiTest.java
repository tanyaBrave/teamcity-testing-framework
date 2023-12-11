package com.example.teamcity.api;

import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.generators.TestData;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.NewProjectDescription;
import com.example.teamcity.api.models.Step;
import com.example.teamcity.api.models.response.ParentProject;
import com.example.teamcity.api.models.response.ProjectResponse;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseApiTest extends BaseTest {
    public TestDataStorage testDataStorage;

    @BeforeMethod
    public void setupTest() {
        testDataStorage = TestDataStorage.getStorage();
    }

    @AfterMethod
    public void cleanTest() {
        testDataStorage.delete();
    }

    protected void archiveProject(String needArchive, String id) {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.TEXT);
        spec.setAccept(ContentType.TEXT);
        new CheckedRequests(spec.build()).getProjectRequest().update(needArchive, id);
    }

    protected void checkProjectIsCreated(ProjectResponse actualProject) {
        softy.assertThat(actualProject).hasNoNullFieldsOrProperties();
        checkedWithSuperUser.getProjectRequest().get("/id:" + actualProject.getId());
    }

    protected void checkProjectIsNotCreated(String locator, String value) {
        var response = uncheckedWithSuperUser.getProjectRequest()
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

    protected void checkCreatedProjectData(ProjectResponse actualProject, NewProjectDescription expectedProject,
                                           String parentProjectId) {
        softy.assertThat(actualProject).extracting(ProjectResponse::getId, ProjectResponse::getName,
                        ProjectResponse::getParentProjectId)
                .containsExactly(expectedProject.getId(), expectedProject.getName(), parentProjectId);
    }

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

    protected void checkBuildConfigIsCreated(BuildType actualBuildConfig) {
        softy.assertThat(actualBuildConfig).hasNoNullFieldsOrProperties();
        checkedWithSuperUser.getBuildConfigRequest().get("/id:" + actualBuildConfig.getId());
    }

    protected void checkBuildConfigIsNotCreated(String locator, String value) {
        var response = uncheckedWithSuperUser.getBuildConfigRequest()
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

    protected void checkCreatedBuildConfigData(BuildType actualBuildConfig, TestData expectedData) {
        var expectedBuildConfig = expectedData.getBuildType();
        var project = expectedData.getProject();
        softy.assertThat(actualBuildConfig).extracting(BuildType::getId, BuildType::getName,
                        BuildType::getProjectId, BuildType::getProjectName)
                .containsExactly(expectedBuildConfig.getId(), expectedBuildConfig.getName(),
                        project.getId(), project.getName());
    }

    protected void checkCreatedBuildConfigStepData(Step actualSteps, Step expectedSteps) {
        softy.assertThat(actualSteps).usingRecursiveComparison()
                .isEqualTo(expectedSteps);
    }
}
