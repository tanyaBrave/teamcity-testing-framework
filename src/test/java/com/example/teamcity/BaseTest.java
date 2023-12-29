package com.example.teamcity;

import com.example.teamcity.api.generators.TestData;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.spec.Specifications;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected SoftAssertions softy;
    public TestDataStorage testDataStorage;
    public CheckedRequests checkedWithSuperUser = new CheckedRequests(Specifications.getSpec().superUserSpec());
    public UncheckedRequests uncheckedWithSuperUser = new UncheckedRequests(Specifications.getSpec().superUserSpec());

    @BeforeMethod
    public void beforeTest() {
        softy = new SoftAssertions();
        testDataStorage = TestDataStorage.getStorage();
    }

    @AfterMethod
    public void afterTest() {
        testDataStorage.delete();
        softy.assertAll();
    }

    protected CheckedRequests getCheckedRequestForUser(User user) {
        return new CheckedRequests(Specifications.getSpec().authSpec(user));
    }

    protected void createProject(TestData testData) {
        new CheckedProject(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getProject());
    }

    protected void checkProjectIsCreated(CheckedRequests request, String locator) {
        request.getProjectRequest().get(locator);
    }

    protected void checkBuildConfigIsCreated(CheckedRequests request, String locator) {
        request.getBuildConfigRequest().get(locator);
    }

    protected void checkBuildConfigIsNotCreated(CheckedRequests request, String locator) {
        var response = request.getProjectRequest().get(locator);
        softy.assertThat(response.getBuildTypes().getBuildType()).isEmpty();
    }
}
