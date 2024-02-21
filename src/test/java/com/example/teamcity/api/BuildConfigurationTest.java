package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Steps;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import data_providers.BaseDataProvider;
import data_providers.BuildConfigDataProvider;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.qameta.allure.testng.Tag;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Collections;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.MINOR;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static io.qameta.allure.SeverityLevel.TRIVIAL;

@Epic("API tests")
@Feature("TeamCity build configuration")
@Story("Creating build configuration")
@Tag("Regression")
public class BuildConfigurationTest extends BaseApiTest {

    @Test(groups = {"Regression"})
    @Description("Creating build configuration without steps should be available")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-8")
    public void creatingBuildConfigurationWithoutStepsShouldBeAvailable() {
        createProject(superUserSpec, testData.getProject());

        var buildConfig = createBuildConfig(superUserSpec, testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test(groups = {"Regression"})
    @Description("Creating build configuration with steps should be available")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-9")
    public void creatingBuildConfigurationWithStepsShouldBeAvailable() {
        var testDataWithBuildSteps = testData;

        createProject(superUserSpec, testDataWithBuildSteps.getProject());

        testDataWithBuildSteps.getBuildType().setSteps(Steps.builder().step(
                        Collections.singletonList(TestDataGenerator.generateBuildConfigSteps(
                                "Print hello world", "simpleRunner",
                                Collections.singletonList(TestDataGenerator.generateStepProperty(
                                        "script.content", "echo 'Hello World!'"))
                        )))
                .build());

        var buildConfig = createBuildConfig(superUserSpec, testDataWithBuildSteps.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testDataWithBuildSteps);
        checkCreatedBuildConfigStepData(buildConfig.getSteps().getStep().get(0),
                testDataWithBuildSteps.getBuildType().getSteps().getStep().get(0));
    }

    @Test(groups = {"Regression"})
    @Description("Creating two build configurations with same name should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-10")
    public void creatingTwoBuildConfigurationWithSameNameShouldNotBeAvailable() {
        createProject(superUserSpec, testData.getProject());

        createBuildConfig(superUserSpec, testData.getBuildType());

        var configWithSameName = testData.getBuildType();
        configWithSameName.setId(RandomData.getString());

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(configWithSameName)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.BUILD_CONFIG_WITH_NAME_ALREADY_EXISTS.getText(),
                        testData.getBuildType().getName())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", configWithSameName.getId());
    }

    @Test(groups = {"Regression"})
    @Description("Creating two build configurations with same name not in the same project should be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-11")
    public void creatingTwoBuildConfigurationWithSameNameInDifferentProjectsShouldBeAvailable() {
        var firstTestData = testData;
        var secondTestData = TestDataGenerator.generate();

        createProject(superUserSpec, firstTestData.getProject());
        createProject(superUserSpec, secondTestData.getProject());

        createBuildConfig(superUserSpec, firstTestData.getBuildType());

        secondTestData.getBuildType().setName(firstTestData.getBuildType().getName());

        var buildConfig =  createBuildConfig(superUserSpec, secondTestData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, secondTestData);
    }

    @Test(groups = {"Regression"})
    @Description("Creating two build configurations with non unique id should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-12")
    public void creatingTwoBuildConfigurationWithNonUniqueIdShouldNotBeAvailable() {
        createProject(superUserSpec, testData.getProject());
        
        createBuildConfig(superUserSpec, testData.getBuildType());

        var configWithSameId = testData.getBuildType();
        configWithSameId.setName(RandomData.getString());

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(configWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.NON_UNIQUE_BUILD_CONFIG_ID.getText(),
                        testData.getBuildType().getId())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "name", configWithSameId.getName());
    }

    @Test(groups = {"Regression"})
    @Description("Creating build configuration with deleted build configuration data should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-13")
    void creatingBuildConfigurationWithDeletedBuildConfigurationDataShouldBeAvailable() {
        createProject(superUserSpec, testData.getProject());

        createBuildConfig(superUserSpec, testData.getBuildType());
        checkedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).delete("/id:" + testData.getBuildType().getId());

        var buildConfig = createBuildConfig(superUserSpec, testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test(groups = {"Regression"})
    @Description("Creating build configuration in archived project should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-14")
    void creatingBuildConfigurationInArchivedProjectShouldBeAvailable() {
        createProject(superUserSpec, testData.getProject());
        
        archiveProject("true", "/id:" + testData.getProject().getId());

        var buildConfig = createBuildConfig(superUserSpec, testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test(groups = {"Regression"})
    @Description("When create build configuration without id it's name will be used as id")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-15")
    void creatingBuildConfigurationWithoutIdWillUseNameAsId() {
        var testDataWithBuildConfigWithoutId = testData;

        createProject(superUserSpec, testDataWithBuildConfigWithoutId.getProject());

        testDataWithBuildConfigWithoutId.getBuildType().setId(null);

        var buildConfig = createBuildConfig(superUserSpec, testDataWithBuildConfigWithoutId.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());

        softy.assertThat(buildConfig.getId())
                .isEqualToIgnoringCase(testDataWithBuildConfigWithoutId.getProject().getId().concat("_")
                                .concat(testDataWithBuildConfigWithoutId.getBuildType().getName().replace("_", "")));
    }

    @Test(groups = {"Regression"})
    @Description("Creating build configuration with xml content-type should be available")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-16")
    void creatingBuildConfigurationWithXmlContentTypeShouldBeAvailable() throws JsonProcessingException {
        createProject(superUserSpec, testData.getProject());

        var buildConfigXml = new XmlMapper().writeValueAsString(testData.getBuildType());

        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.XML);
        spec.setAccept(ContentType.XML);

        new UncheckedRequests(spec.build()).getRequest(Endpoint.BUILD_TYPES).create(buildConfigXml)
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test(groups = {"Regression"}, dataProvider = "validId", dataProviderClass = BaseDataProvider.class)
    @Description("Creating build configuration with valid id should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-17")
    void creatingBuildConfigurationWithValidIdShouldBeAvailable(String id) {
        var testDataWithValidId = testData;

        createProject(superUserSpec, testDataWithValidId.getProject());

        testDataWithValidId.getBuildType().setId(id);

        var buildConfig = createBuildConfig(superUserSpec, testDataWithValidId.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    //TODO Узнать maxLength для name, тест проходит со значением 10000000
    @Test(groups = {"Regression"}, dataProvider = "validName", dataProviderClass = BaseDataProvider.class)
    @Description("Creating build configuration with valid name should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-18")
    @Issue("TC-IMPROVEMENT-1")
    void creatingBuildConfigurationWithValidNameShouldBeAvailable(String name) {
        var testDataWithValidName = testData;

        createProject(superUserSpec, testDataWithValidName.getProject());

        testDataWithValidName.getBuildType().setName(name);

        var buildConfig = createBuildConfig(superUserSpec, testDataWithValidName.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    @Test(groups = {"Regression"}, dataProvider = "validId", dataProviderClass = BaseDataProvider.class)
    @Description("Creating build configuration with valid project id should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-19")
    void creatingBuildConfigurationWithValidProjectIdShouldBeAvailable(String id) {
        var testDataWithValidProjectId = testData;

        testDataWithValidProjectId.getProject().setId(id);

        createProject(superUserSpec, testDataWithValidProjectId.getProject());

        testDataWithValidProjectId.getBuildType().getProject().setId(id);

        var buildConfig = createBuildConfig(superUserSpec, testDataWithValidProjectId.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    @Test(groups = {"Regression"})
    @Description("Creating build configuration in non existed project should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-20")
    void creatingBuildConfigurationInNonExistedProjectShouldNotBeAvailable() {
        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString(String.format(Errors.PROJECT_NOT_FOUND_BY_LOCATOR.getText(),
                        testData.getProject().getId())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", testData.getBuildType().getId());
    }

    @Test(groups = {"Regression"}, dataProvider = "invalidId", dataProviderClass = BuildConfigDataProvider.class)
    @Description("Creating build configuration with invalid id should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-21")
    @Issue("TC-BUGS-1")
    void creatingBuildConfigurationWithInvalidIdShouldNotBeAvailable(String id, String error) {
        var testDataWithInvalidId = testData;

        createProject(superUserSpec, testDataWithInvalidId.getProject());

        testDataWithInvalidId.getBuildType().setId(id);

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testDataWithInvalidId.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

    }

    @Test(groups = {"Regression"}, dataProvider = "invalidName", dataProviderClass = BuildConfigDataProvider.class)
    @Description("Creating build configuration with invalid name should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-22")
    void creatingBuildConfigurationWithInvalidNameShouldNotBeAvailable(String name) {
        var testDataWithInvalidName = testData;

        createProject(superUserSpec, testDataWithInvalidName.getProject());

        testDataWithInvalidName.getBuildType().setName(name);

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testDataWithInvalidName.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.EMPTY_BUILD_CONFIG_NAME.getText()));
    }

    @Test(groups = {"Regression"}, dataProvider = "invalidProjectId", dataProviderClass = BuildConfigDataProvider.class)
    @Description("Creating build configuration with invalid project id should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-23")
    @Issue("TC-BUGS-2")
    void creatingBuildConfigurationWithInvalidProjectIdShouldNotBeAvailable(String id, String error) {
        var testDataWithInvalidProjectId = testData;

        testDataWithInvalidProjectId.getBuildType().getProject().setId(id);

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testDataWithInvalidProjectId.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));
    }

    @Test(groups = {"Regression"})
    @Description("Creating build configuration with empty body should not be available")
    @Severity(TRIVIAL)
    @TmsLink("TC-TMS-24")
    void creatingBuildConfigurationWithEmptyBodyShouldNotBeAvailable() {
        var testDataWithEmptyBuildType = testData;

        testDataWithEmptyBuildType.setBuildType(BuildType.builder().build());

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testDataWithEmptyBuildType.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.BUILD_TYPE_WITHOUT_PROJECT_NODE.getText()));
    }

    @Test(groups = {"Regression"})
    @Description("Creating build configuration with invalid Accept header should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-25")
    void creatingBuildConfigurationWithInvalidAcceptHeaderShouldNotBeAvailable() {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setAccept(ContentType.TEXT);

        new UncheckedRequests(spec.build()).getRequest(Endpoint.BUILD_TYPES).create(RandomData.getString())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_ACCEPTABLE);
    }
}
