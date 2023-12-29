package com.example.teamcity.api;

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
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test
    public void creatingBuildConfigurationWithStepsShouldBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getBuildType().setSteps(Steps.builder().step(
                        Collections.singletonList(TestDataGenerator.generateBuildConfigSteps(
                                "Print hello world", "simpleRunner",
                                Collections.singletonList(TestDataGenerator.generateStepProperty(
                                        "script.content", "echo 'Hello World!'"))
                        )))
                .build());

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
        checkCreatedBuildConfigStepData(buildConfig.getSteps().getStep().get(0),
                testData.getBuildType().getSteps().getStep().get(0));
    }

    @Test
    public void creatingTwoBuildConfigurationWithSameNameShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        var configWithSameName = testData.getBuildType();
        configWithSameName.setId(RandomData.getString());

        uncheckedWithSuperUser.getBuildConfigRequest().create(configWithSameName)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.BUILD_CONFIG_WITH_NAME_ALREADY_EXISTS.getText(),
                        testData.getBuildType().getName())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", configWithSameName.getId());
    }

    @Test
    public void creatingTwoBuildConfigurationWithSameNameInDifferentProjectsShouldBeAvailable() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(firstTestData.getProject());
        checkedWithSuperUser.getProjectRequest().create(secondTestData.getProject());

        checkedWithSuperUser.getBuildConfigRequest().create(firstTestData.getBuildType());

        secondTestData.getBuildType().setName(firstTestData.getBuildType().getName());

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(secondTestData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, secondTestData);
    }

    @Test
    public void creatingTwoBuildConfigurationWithNonUniqueIdShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        var configWithSameId = testData.getBuildType();
        configWithSameId.setName(RandomData.getString());

        uncheckedWithSuperUser.getBuildConfigRequest().create(configWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.NON_UNIQUE_BUILD_CONFIG_ID.getText(),
                        testData.getBuildType().getId())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "name", configWithSameId.getName());
    }

    @Test
    void creatingBuildConfigurationWithDeletedBuildConfigurationDataShouldBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());
        checkedWithSuperUser.getBuildConfigRequest().delete(testData.getBuildType().getId());

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test
    void creatingBuildConfigurationInArchivedProjectShouldBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());
        archiveProject("true", testData.getProject().getId());

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
        checkCreatedBuildConfigData(buildConfig, testData);
    }

    @Test
    void creatingBuildConfigurationWithoutIdWillUseNameAsId() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getBuildType().setId(null);

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());

        softy.assertThat(buildConfig.getId())
                .isEqualToIgnoringCase(testData.getProject().getId().concat("_")
                                .concat(testData.getBuildType().getName().replace("_", "")));
    }

    @Test
    void creatingBuildConfigurationWithXmlContentTypeShouldBeAvailable() throws JsonProcessingException {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        var buildConfigXml = new XmlMapper().writeValueAsString(testData.getBuildType());

        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.XML);
        spec.setAccept(ContentType.XML);

        new UncheckedRequests(spec.build()).getBuildConfigRequest().create(buildConfigXml)
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test(dataProvider = "validId", dataProviderClass = BaseDataProvider.class)
    void creatingBuildConfigurationWithValidIdShouldBeAvailable(String id) {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getBuildType().setId(id);

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    //TODO Узнать maxLength для name, тест проходит со значением 10000000
    @Test(dataProvider = "validName", dataProviderClass = BaseDataProvider.class)
    void creatingBuildConfigurationWithValidNameShouldBeAvailable(String name) {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getBuildType().setName(name);

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    @Test(dataProvider = "validId", dataProviderClass = BaseDataProvider.class)
    void creatingBuildConfigurationWithValidProjectIdShouldBeAvailable(String id) {
        var testData = testDataStorage.addTestData();

        testData.getProject().setId(id);

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getBuildType().getProject().setId(id);

        var buildConfig = checkedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType());

        checkBuildConfigIsCreated(checkedWithSuperUser, "/id:" + buildConfig.getId());
    }

    @Test
    void creatingBuildConfigurationInNonExistedProjectShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        uncheckedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString(String.format(Errors.PROJECT_NOT_FOUND_BY_LOCATOR.getText(),
                        testData.getProject().getId())));

        checkBuildConfigIsNotCreated(uncheckedWithSuperUser, "id", testData.getBuildType().getId());
    }

    @Test(dataProvider = "invalidId", dataProviderClass = BuildConfigDataProvider.class)
    void creatingBuildConfigurationWithInvalidIdShouldNotBeAvailable(String id, String error) {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getBuildType().setId(id);

        uncheckedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

    }

    @Test(dataProvider = "invalidName", dataProviderClass = BuildConfigDataProvider.class)
    void creatingBuildConfigurationWithInvalidNameShouldNotBeAvailable(String name) {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getBuildType().setName(name);

        uncheckedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.EMPTY_BUILD_CONFIG_NAME.getText()));
    }

    @Test(dataProvider = "invalidProjectId", dataProviderClass = BuildConfigDataProvider.class)
    void creatingBuildConfigurationWithInvalidProjectIdShouldNotBeAvailable(String id, String error) {
        var testData = testDataStorage.addTestData();

        testData.getBuildType().getProject().setId(id);

        uncheckedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));
    }

    @Test
    void creatingBuildConfigurationWithEmptyBodyShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        testData.setBuildType(BuildType.builder().build());

        uncheckedWithSuperUser.getBuildConfigRequest().create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.BUILD_TYPE_WITHOUT_PROJECT_NODE.getText()));
    }

    @Test
    void creatingBuildConfigurationWithInvalidAcceptHeaderShouldNotBeAvailable() {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setAccept(ContentType.TEXT);

        new UncheckedRequests(spec.build()).getBuildConfigRequest().create(RandomData.getString())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_ACCEPTABLE);
    }
}
