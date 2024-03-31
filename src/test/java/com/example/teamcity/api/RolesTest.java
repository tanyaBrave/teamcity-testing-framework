package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.enums.Role;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.qameta.allure.testng.Tag;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.NORMAL;

@Epic("API tests")
@Feature("TeamCity role model")
@Story("Roles permission")
@Tag("Regression")
public class RolesTest extends BaseApiTest {

    @Test(groups = {"Regression"})
    @Description("Unauthorized user should not have rights to create project")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-1")
    public void unauthorizedUserShouldNotHaveRightsToCreateProject() {
        new UncheckedRequests(Specifications.getSpec().unAuthSpec())
                .getRequest(Endpoint.PROJECTS)
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(Matchers.equalTo("Authentication required\nTo login manually go to \"/login.html\" page"));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", testData.getProject().getId());
    }

    @Test(groups = {"Regression"})
    @Description("System admin should have rights to create project")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-2")
    public void systemAdminShouldHaveRightsToCreateProject() {
        var testDataForSystemAdmin = testData;

        testDataForSystemAdmin.getUser().setRoles(TestDataGenerator.generateRoles(Role.SYSTEM_ADMIN, "g"));

        checkedWithSuperUser.getRequest(Endpoint.USERS).create(testDataForSystemAdmin.getUser());

        var project = createProject(Specifications.getSpec().authSpec(testDataForSystemAdmin.getUser()),
                testDataForSystemAdmin.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
    }

    @Test(groups = {"Regression"})
    @Description("Non admin should not have rights to create project")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-3")
    public void nonAdminShouldNotHaveRightsToCreateProject() {
        var firstTestData = testData;
        var secondTestData = TestDataGenerator.generate();

        checkedWithSuperUser.getRequest(Endpoint.PROJECTS).create(firstTestData.getProject());

        firstTestData.getUser().setRoles(TestDataGenerator.generateRoles(Role.PROJECT_DEVELOPER,
                "p:" + firstTestData.getProject().getId()));

        secondTestData.getProject().getParentProject().setLocator(firstTestData.getProject().getId());

        checkedWithSuperUser.getRequest(Endpoint.USERS).create(firstTestData.getUser());

        new UncheckedRequests(Specifications.getSpec().authSpec(firstTestData.getUser()))
                .getRequest(Endpoint.PROJECTS)
                .create(secondTestData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(Errors.NO_PERM_TO_CREATE_PROJECT.getText()));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", secondTestData.getProject().getId());
    }

    @Test(groups = {"Regression"})
    @Description("Unauthorized user should not have rights to create build configuration")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-4")
    public void unauthorizedUserShouldNotHaveRightsToCreateBuildConfig() {
        createProject(superUserSpec, testData.getProject());

        new UncheckedRequests(Specifications.getSpec().unAuthSpec())
                .getRequest(Endpoint.BUILD_TYPES)
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(Matchers.equalTo("Authentication required\nTo login manually go to \"/login.html\" page"));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", testData.getBuildType().getId());
    }

    @Test(groups = {"Regression"})
    @Description("Project admin should have rights to create build configuration")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-5")
    public void projectAdminShouldHaveRightsToCreateBuildConfigToHisProject() {
        var testDataForProjectAdmin = testData;

        createProject(superUserSpec, testDataForProjectAdmin.getProject());

        testDataForProjectAdmin.getUser().setRoles(TestDataGenerator
                .generateRoles(Role.PROJECT_ADMIN, "p:" + testDataForProjectAdmin.getProject().getId()));

        checkedWithSuperUser.getRequest(Endpoint.USERS).create(testDataForProjectAdmin.getUser());

        var buildConfig = createBuildConfig(Specifications.getSpec().authSpec(testDataForProjectAdmin.getUser()),
                testDataForProjectAdmin.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    @Test(groups = {"Regression"})
    @Description("Project admin should not have rights to create build configuration not in his project")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-6")
    public void projectAdminShouldNotHaveRightsToCreateBuildConfigToAnotherProject() {
        var firstTestData = testData;
        var secondTestData = TestDataGenerator.generate();

        createProject(superUserSpec, firstTestData.getProject());
        createProject(superUserSpec, secondTestData.getProject());

        firstTestData.getUser().setRoles(TestDataGenerator
                .generateRoles(Role.PROJECT_ADMIN, "p:" + firstTestData.getProject().getId()));

        checkedWithSuperUser.getRequest(Endpoint.USERS).create(firstTestData.getUser());

        secondTestData.getUser().setRoles(TestDataGenerator
                .generateRoles(Role.PROJECT_ADMIN, "p:" + secondTestData.getProject().getId()));

        checkedWithSuperUser.getRequest(Endpoint.USERS).create(secondTestData.getUser());

        new UncheckedRequests(Specifications.getSpec().authSpec(secondTestData.getUser()))
                .getRequest(Endpoint.BUILD_TYPES)
                .create(firstTestData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST);

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", firstTestData.getBuildType().getId());
    }

    @Test(groups = {"Regression"})
    @Description("Non admin should not have rights to create build configuration")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-7")
    public void nonAdminShouldNotHaveRightsToCreateBuildConfig() {
        var testDataForNonAdmin = testData;

        createProject(superUserSpec, testDataForNonAdmin.getProject());

        testDataForNonAdmin.getUser().setRoles(TestDataGenerator.generateRoles(Role.PROJECT_DEVELOPER,
                "p:" + testDataForNonAdmin.getProject().getId()));

        checkedWithSuperUser.getRequest(Endpoint.USERS).create(testDataForNonAdmin.getUser());

        new UncheckedRequests(Specifications.getSpec().authSpec(testDataForNonAdmin.getUser()))
                .getRequest(Endpoint.BUILD_TYPES)
                .create(testDataForNonAdmin.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(String.format(Errors.NO_PERM_TO_EDIT_PROJECT.getText(),
                        testDataForNonAdmin.getProject().getId())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", testDataForNonAdmin.getBuildType().getId());
    }
}
