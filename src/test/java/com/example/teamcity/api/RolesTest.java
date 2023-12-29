package com.example.teamcity.api;

import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.enums.Role;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.requests.checked.CheckedBuildConfig;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.requests.unchecked.UncheckedBuildConfig;
import com.example.teamcity.api.requests.unchecked.UncheckedProject;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class RolesTest extends BaseApiTest {

    @Test
    public void unauthorizedUserShouldNotHaveRightsToCreateProject() {
        var testData = testDataStorage.addTestData();

        new UncheckedProject(Specifications.getSpec().unAuthSpec())
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(Matchers.equalTo("Authentication required\nTo login manually go to \"/login.html\" page"));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", testData.getProject().getId());
    }

    @Test
    public void systemAdminShouldHaveRightsToCreateProject() {
        var testData = testDataStorage.addTestData();

        testData.getUser().setRoles(TestDataGenerator.generateRoles(Role.SYSTEM_ADMIN, "g"));

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        var project = new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        checkProjectIsCreated(checkedWithSuperUser, "/id:" + project.getId());
    }

    @Test
    public void nonAdminShouldNotHaveRightsToCreateProject() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(firstTestData.getProject());

        firstTestData.getUser().setRoles(TestDataGenerator.generateRoles(Role.PROJECT_DEVELOPER,
                "p:" + firstTestData.getProject().getId()));

        secondTestData.getProject().getParentProject().setLocator(firstTestData.getProject().getId());

        checkedWithSuperUser.getUserRequest().create(firstTestData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(firstTestData.getUser()))
                .create(secondTestData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(Errors.NO_PERM_TO_CREATE_PROJECT.getText()));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", secondTestData.getProject().getId());
    }

    @Test
    public void unauthorizedUserShouldNotHaveRightsToCreateBuildConfig() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        new UncheckedBuildConfig(Specifications.getSpec().unAuthSpec())
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(Matchers.equalTo("Authentication required\nTo login manually go to \"/login.html\" page"));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", testData.getBuildType().getId());
    }

    @Test
    public void projectAdminShouldHaveRightsToCreateBuildConfigToHisProject() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getUser().setRoles(TestDataGenerator
                .generateRoles(Role.PROJECT_ADMIN, "p:" + testData.getProject().getId()));

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        var buildConfig = new CheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    @Test
    public void projectAdminShouldNotHaveRightsToCreateBuildConfigToAnotherProject() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(firstTestData.getProject());
        checkedWithSuperUser.getProjectRequest().create(secondTestData.getProject());

        firstTestData.getUser().setRoles(TestDataGenerator
                .generateRoles(Role.PROJECT_ADMIN, "p:" + firstTestData.getProject().getId()));

        checkedWithSuperUser.getUserRequest().create(firstTestData.getUser());

        secondTestData.getUser().setRoles(TestDataGenerator
                .generateRoles(Role.PROJECT_ADMIN, "p:" + secondTestData.getProject().getId()));

        checkedWithSuperUser.getUserRequest().create(secondTestData.getUser());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(secondTestData.getUser()))
                .create(firstTestData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST);

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", firstTestData.getBuildType().getId());
    }

    @Test
    public void nonAdminShouldNotHaveRightsToCreateBuildConfig() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getUser().setRoles(TestDataGenerator.generateRoles(Role.PROJECT_DEVELOPER,
                "p:" + testData.getProject().getId()));

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(String.format(Errors.NO_PERM_TO_EDIT_PROJECT.getText(),
                        testData.getProject().getId())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", testData.getBuildType().getId());
    }
}
