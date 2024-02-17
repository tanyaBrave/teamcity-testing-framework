package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.models.NewProjectDescription;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import data_providers.BaseDataProvider;
import data_providers.ProjectsDataProvider;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class ProjectTest extends BaseApiTest {

    @Test(groups = {"Regression"})
    void creatingProjectInRootShouldBeAvailable() {
        var project = createProject(superUserSpec, testData.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, testData.getProject(), testData.getProject().getParentProject().getLocator());
        checkParentProject(project.getParentProject(), "_Root", null);
    }

    @Test(groups = {"Regression"})
    void creatingProjectNotInRootShouldBeAvailable() {
        var firstTestData = testData;
        var secondTestData = TestDataGenerator.generate();

        var parentProject = createProject(superUserSpec, firstTestData.getProject());

        secondTestData.getProject().getParentProject().setLocator(firstTestData.getProject().getId());

        var project = createProject(superUserSpec, secondTestData.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, secondTestData.getProject(),
                secondTestData.getProject().getParentProject().getLocator());
        checkParentProject(project.getParentProject(), parentProject.getId(), parentProject.getName());
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithNameAsLocatorShouldBeAvailable() {
        var firstTestData = testData;
        var secondTestData = TestDataGenerator.generate();

        createProject(superUserSpec, firstTestData.getProject());

        secondTestData.getProject().getParentProject().setLocator(firstTestData.getProject().getName());

        var project = createProject(superUserSpec, secondTestData.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, secondTestData.getProject(), firstTestData.getProject().getId());
        checkParentProject(project.getParentProject(), firstTestData.getProject().getId(),
                firstTestData.getProject().getName());
    }

    @Test(groups = {"Regression"})
    void creatingTwoProjectsWithSameNameShouldNotBeAvailable() {
        createProject(superUserSpec, testData.getProject());

        var projectWithSameName = testData.getProject();
        projectWithSameName.setId(RandomData.getString());

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(projectWithSameName)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.PROJECT_WITH_NAME_ALREADY_EXISTS.getText(),
                        testData.getProject().getName())));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", testData.getProject().getId());
    }

    @Test(groups = {"Regression"})
    void creatingTwoProjectsWithSameNameInDifferentParentsShouldBeAvailable() {
        var secondTestData = TestDataGenerator.generate();

        var firstProject = createProject(superUserSpec, testData.getProject());
        var secondProject = createProject(superUserSpec, secondTestData.getProject());

        var projectWithSameNameInSecondParent = TestDataGenerator.generateProject(secondProject.getId(),
                true);
        projectWithSameNameInSecondParent.setName(firstProject.getName());

        var project = createProject(superUserSpec, projectWithSameNameInSecondParent);

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, projectWithSameNameInSecondParent,
                projectWithSameNameInSecondParent.getParentProject().getLocator());
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithNonUniqueIdShouldNotBeAvailable() {
        createProject(superUserSpec, testData.getProject());

        var projectWithSameId = testData.getProject();
        projectWithSameId.setName(RandomData.getString());

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.NON_UNIQUE_PROJECT_ID.getText(),
                        testData.getProject().getId())));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "name", projectWithSameId.getName());
    }

    @Test(groups = {"Regression"}, dataProvider = "withoutCopySettings", dataProviderClass = ProjectsDataProvider.class)
    void creatingProjectWithoutCopyAllAssociatedSettingsShouldBeAvailable(Boolean value) {
        var testDataForCopySettings = testData;

        testDataForCopySettings.getProject().setCopyAllAssociatedSettings(value);

        var project = createProject(superUserSpec, testDataForCopySettings.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, testDataForCopySettings.getProject(),
                testDataForCopySettings.getProject().getParentProject().getLocator());
        checkParentProject(project.getParentProject(), "_Root", null);
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithoutIdWillUseNameAsId() {
        var testDataWithNullId = testData;

        testDataWithNullId.getProject().setId(null);

        var project = createProject(superUserSpec, testDataWithNullId.getProject());

        softy.assertThat(project.getId())
                .isEqualToIgnoringCase(testDataWithNullId.getProject().getName().replace("_", ""));
        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithoutParentWillBeCreatedInRoot() {
        var testDataWithNullParentProject = testData;

        testDataWithNullParentProject.getProject().setParentProject(null);

        var project = createProject(superUserSpec, testDataWithNullParentProject.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, testDataWithNullParentProject.getProject(), "_Root");
        checkParentProject(project.getParentProject(), "_Root", null);
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithDeletedProjectsDataShouldBeAvailable() {
        createProject(superUserSpec, testData.getProject());
        checkedWithSuperUser.getRequest(Endpoint.PROJECTS).delete("/id:" + testData.getProject().getId());

        var project = createProject(superUserSpec, testData.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithArchivedProjectsDataShouldNotBeAvailable() {
        createProject(superUserSpec, testData.getProject());
        archiveProject("true", "/id:" + testData.getProject().getId());

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.PROJECT_WITH_NAME_ALREADY_EXISTS.getText(),
                        testData.getProject().getName())));
    }

    //TODO Узнать maxLength для name и locator, тест проходит со значением 10000000
    @Test(groups = {"Regression"}, dataProvider = "validName", dataProviderClass = BaseDataProvider.class)
    void creatingProjectWithValidNameAsLocatorShouldBeAvailable(String locator) {
        var parentTestData = testData;
        var projectTestData = TestDataGenerator.generate();

        parentTestData.getProject().setName(locator);

        var parentProject = createProject(superUserSpec, parentTestData.getProject());

        projectTestData.getProject().getParentProject().setLocator(parentProject.getName());

        var project = createProject(superUserSpec, projectTestData.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, projectTestData.getProject(), parentTestData.getProject().getId());
        checkParentProject(project.getParentProject(), parentTestData.getProject().getId(),
                parentTestData.getProject().getName());
    }

    @Test(groups = {"Regression"}, dataProvider = "validId", dataProviderClass = BaseDataProvider.class)
    void creatingProjectWithValidIdShouldBeAvailable(String id) {
        var testDataWithValidId = testData;

        testDataWithValidId.getProject().setId(id);

        var project = createProject(superUserSpec, testDataWithValidId.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, testDataWithValidId.getProject(), testDataWithValidId.getProject().getParentProject().getLocator());
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithXmlContentTypeShouldBeAvailable() throws JsonProcessingException {

        var projectXml = new XmlMapper().writeValueAsString(testData.getProject());

        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.XML);
        spec.setAccept(ContentType.XML);

        new UncheckedRequests(spec.build()).getRequest(Endpoint.PROJECTS).create(projectXml)
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithNonExistedLocatorShouldNotBeAvailable() {
        var testDataWithNonExistedLocator = testData;

        testDataWithNonExistedLocator.getProject().getParentProject().setLocator(RandomData.getString());

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testDataWithNonExistedLocator.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString(String.format(Errors.PROJECT_NOT_FOUND_BY_NAME_OR_ID.getText(),
                        testDataWithNonExistedLocator.getProject().getParentProject().getLocator())));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", testDataWithNonExistedLocator.getProject().getId());
    }


    @Test(groups = {"Regression"}, dataProvider = "invalidLocator", dataProviderClass = ProjectsDataProvider.class)
    void creatingProjectWithInvalidLocatorShouldNotBeAvailable(String locator, int code, String error) {
        var testDataWithInvalidLocator = testData;

        testDataWithInvalidLocator.getProject().getParentProject().setLocator(locator);

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testDataWithInvalidLocator.getProject())
                .then().assertThat().statusCode(code)
                .body(Matchers.containsString(error));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", testDataWithInvalidLocator.getProject().getId());
    }

    /*
        Возможно, это by design, но при передаче пустой строки стоит тоже возвращать 400, а не 500.
        И текст ошибки как при отсутствии параметра, чтобы не плодить тексты ошибок с одинаковым смыслом
        (для id возращается одна и та же ошибка)
     */
    @Test(groups = {"Regression"}, dataProvider = "invalidName", dataProviderClass = ProjectsDataProvider.class)
    void creatingProjectWithInvalidNameShouldNotBeAvailable(String name, String error) {
        var testDataWithInvalidName = testData;

        testDataWithInvalidName.getProject().setName(name);

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testDataWithInvalidName.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", testDataWithInvalidName.getProject().getId());
    }

    @Test(groups = {"Regression"}, dataProvider = "invalidId", dataProviderClass = ProjectsDataProvider.class)
    void creatingProjectWithInvalidIdShouldNotBeAvailable(String id, String error) {
        var testDataWithInvalidId = testData;

        testDataWithInvalidId.getProject().setId(id);

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testDataWithInvalidId.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "name", testDataWithInvalidId.getProject().getName());
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithEmptyBodyShouldNotBeAvailable() {
        var testDataWithEmptyProject = testData;

        testDataWithEmptyProject.setProject(NewProjectDescription.builder().build());

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testDataWithEmptyProject.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.EMPTY_PROJECT_NAME.getText()));
    }

    @Test(groups = {"Regression"})
    void creatingProjectWithInvalidAcceptHeaderShouldNotBeAvailable() {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setAccept(ContentType.TEXT);

        new UncheckedRequests(spec.build()).getRequest(Endpoint.PROJECTS).create(RandomData.getString())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_ACCEPTABLE);
    }
}
