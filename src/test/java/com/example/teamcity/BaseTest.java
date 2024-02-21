package com.example.teamcity;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.generators.TestData;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.NewProjectDescription;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.models.response.ProjectResponse;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected SoftAssertions softy;
    protected TestData testData;
    protected final CheckedRequests checkedWithSuperUser = new CheckedRequests(Specifications.getSpec().superUserSpec());
    protected final RequestSpecification superUserSpec = Specifications.getSpec().superUserSpec();
    protected final UncheckedRequests uncheckedWithSuperUser = new UncheckedRequests(Specifications.getSpec().superUserSpec());

    @BeforeMethod(alwaysRun = true)
    @Step("Setup assertions and test data")
    public void beforeTest() {
        softy = new SoftAssertions();
        testData = TestDataGenerator.generate();
    }

    @AfterMethod(alwaysRun = true)
    @Step("Clean up test data and check assertions")
    public void afterTest() {
        TestDataStorage.getStorage().deleteCreatedEntities();
        softy.assertAll();
    }

    @Step("Prepare checked request for user")
    protected CheckedRequests getCheckedRequestForUser(User user) {
        return new CheckedRequests(Specifications.getSpec().authSpec(user));
    }

    @Step("Get auth specification for user")
    protected RequestSpecification getSpecForUser(User user) {
        return Specifications.getSpec().authSpec(user);
    }

    @Step("Create project")
    protected void createProject(TestData testData) {
        new CheckedRequests(Specifications.getSpec().authSpec(testData.getUser()))
                .getRequest(Endpoint.PROJECTS)
                .create(testData.getProject());
    }

    @Step("Create project")
    protected ProjectResponse createProject(RequestSpecification spec, NewProjectDescription projectDescription) {
        return (ProjectResponse) new CheckedRequests(spec)
                .getRequest(Endpoint.PROJECTS)
                .create(projectDescription);
    }

    @Step("Check that project with locator: {1} was created")
    protected void checkProjectIsCreated(RequestSpecification spec, String locator) {
        new CheckedRequests(spec)
                .getRequest(Endpoint.PROJECTS)
                .get(locator);
    }

    @Step("Check that build configuration with locator: {1} was created")
    protected void checkBuildConfigIsCreated(CheckedRequests request, String locator) {
        request.getRequest(Endpoint.BUILD_TYPES).get(locator);
    }

    @Step("Check that build configuration with locator: {1} was not created")
    protected void checkBuildConfigIsNotCreated(CheckedRequests request, String locator) {
        var response = (ProjectResponse) request.getRequest(Endpoint.PROJECTS).get(locator);
        softy.assertThat(response.getBuildTypes().getBuildType()).isEmpty();
    }
}
