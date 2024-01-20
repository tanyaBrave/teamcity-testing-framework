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
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Collections;

public class BuildConfigurationTest extends BaseApiTest {

    @Test
    public void creatingBuildConfigurationWithoutStepsShouldBeAvailable() {
        createProject(superUserSpec, testData.getProject());

        var buildConfig = createBuildConfig(superUserSpec, testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    void creatingBuildConfigurationWithDeletedBuildConfigurationDataShouldBeAvailable() {
        createProject(superUserSpec, testData.getProject());

        createBuildConfig(superUserSpec, testData.getBuildType());
        checkedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).delete("/id:" + testData.getBuildType().getId());

        var buildConfig = createBuildConfig(superUserSpec, testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test
    void creatingBuildConfigurationInArchivedProjectShouldBeAvailable() {
        createProject(superUserSpec, testData.getProject());
        
        archiveProject("true", "/id:" + testData.getProject().getId());

        var buildConfig = createBuildConfig(superUserSpec, testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test
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

    @Test
    void creatingBuildConfigurationWithXmlContentTypeShouldBeAvailable() throws JsonProcessingException {
        createProject(superUserSpec, testData.getProject());

        var buildConfigXml = new XmlMapper().writeValueAsString(testData.getBuildType());

        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.XML);
        spec.setAccept(ContentType.XML);

        new UncheckedRequests(spec.build()).getRequest(Endpoint.BUILD_TYPES).create(buildConfigXml)
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test(dataProvider = "validId", dataProviderClass = BaseDataProvider.class)
    void creatingBuildConfigurationWithValidIdShouldBeAvailable(String id) {
        var testDataWithValidId = testData;

        createProject(superUserSpec, testDataWithValidId.getProject());

        testDataWithValidId.getBuildType().setId(id);

        var buildConfig = createBuildConfig(superUserSpec, testDataWithValidId.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    //TODO Узнать maxLength для name, тест проходит со значением 10000000
    @Test(dataProvider = "validName", dataProviderClass = BaseDataProvider.class)
    void creatingBuildConfigurationWithValidNameShouldBeAvailable(String name) {
        var testDataWithValidName = testData;

        createProject(superUserSpec, testDataWithValidName.getProject());

        testDataWithValidName.getBuildType().setName(name);

        var buildConfig = createBuildConfig(superUserSpec, testDataWithValidName.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    @Test(dataProvider = "validId", dataProviderClass = BaseDataProvider.class)
    void creatingBuildConfigurationWithValidProjectIdShouldBeAvailable(String id) {
        var testDataWithValidProjectId = testData;

        testDataWithValidProjectId.getProject().setId(id);

        createProject(superUserSpec, testDataWithValidProjectId.getProject());

        testDataWithValidProjectId.getBuildType().getProject().setId(id);

        var buildConfig = createBuildConfig(superUserSpec, testDataWithValidProjectId.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    @Test
    void creatingBuildConfigurationInNonExistedProjectShouldNotBeAvailable() {
        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString(String.format(Errors.PROJECT_NOT_FOUND_BY_LOCATOR.getText(),
                        testData.getProject().getId())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", testData.getBuildType().getId());
    }

    @Test(dataProvider = "invalidId", dataProviderClass = BuildConfigDataProvider.class)
    void creatingBuildConfigurationWithInvalidIdShouldNotBeAvailable(String id, String error) {
        var testDataWithInvalidId = testData;

        createProject(superUserSpec, testDataWithInvalidId.getProject());

        testDataWithInvalidId.getBuildType().setId(id);

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testDataWithInvalidId.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

    }

    @Test(dataProvider = "invalidName", dataProviderClass = BuildConfigDataProvider.class)
    void creatingBuildConfigurationWithInvalidNameShouldNotBeAvailable(String name) {
        var testDataWithInvalidName = testData;

        createProject(superUserSpec, testDataWithInvalidName.getProject());

        testDataWithInvalidName.getBuildType().setName(name);

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testDataWithInvalidName.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.EMPTY_BUILD_CONFIG_NAME.getText()));
    }

    @Test(dataProvider = "invalidProjectId", dataProviderClass = BuildConfigDataProvider.class)
    void creatingBuildConfigurationWithInvalidProjectIdShouldNotBeAvailable(String id, String error) {
        var testDataWithInvalidProjectId = testData;

        testDataWithInvalidProjectId.getBuildType().getProject().setId(id);

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testDataWithInvalidProjectId.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));
    }

    @Test
    void creatingBuildConfigurationWithEmptyBodyShouldNotBeAvailable() {
        var testDataWithEmptyBuildType = testData;

        testDataWithEmptyBuildType.setBuildType(BuildType.builder().build());

        uncheckedWithSuperUser.getRequest(Endpoint.BUILD_TYPES).create(testDataWithEmptyBuildType.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.BUILD_TYPE_WITHOUT_PROJECT_NODE.getText()));
    }

    @Test
    void creatingBuildConfigurationWithInvalidAcceptHeaderShouldNotBeAvailable() {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setAccept(ContentType.TEXT);

        new UncheckedRequests(spec.build()).getRequest(Endpoint.BUILD_TYPES).create(RandomData.getString())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_ACCEPTABLE);
    }
}
