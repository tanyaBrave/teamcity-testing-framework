package com.example.teamcity.api;

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

    @Test
    void creatingProjectInRootShouldBeAvailable() {
        var testData = testDataStorage.addTestData();

        var project = checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        checkProjectIsCreated(project);
        checkCreatedProjectData(project, testData.getProject(), testData.getProject().getParentProject().getLocator());
        checkParentProject(project.getParentProject(), "_Root", null);
    }

    @Test
    void creatingProjectNotInRootShouldBeAvailable() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        var parentProject = checkedWithSuperUser.getProjectRequest().create(firstTestData.getProject());

        secondTestData.getProject().getParentProject().setLocator(firstTestData.getProject().getId());

        var project = checkedWithSuperUser.getProjectRequest().create(secondTestData.getProject());

        checkProjectIsCreated(project);
        checkCreatedProjectData(project, secondTestData.getProject(),
                secondTestData.getProject().getParentProject().getLocator());
        checkParentProject(project.getParentProject(), parentProject.getId(), parentProject.getName());
    }

    @Test
    void creatingProjectWithNameAsLocatorShouldBeAvailable() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(firstTestData.getProject());

        secondTestData.getProject().getParentProject().setLocator(firstTestData.getProject().getName());

        var project = checkedWithSuperUser.getProjectRequest().create(secondTestData.getProject());

        checkProjectIsCreated(project);
        checkCreatedProjectData(project, secondTestData.getProject(), firstTestData.getProject().getId());
        checkParentProject(project.getParentProject(), firstTestData.getProject().getId(),
                firstTestData.getProject().getName());
    }

    @Test
    void creatingTwoProjectsWithSameNameShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        var projectWithSameName = testData.getProject();
        projectWithSameName.setId(RandomData.getString());

        uncheckedWithSuperUser.getProjectRequest().create(projectWithSameName)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.PROJECT_WITH_NAME_ALREADY_EXISTS.getText(),
                        testData.getProject().getName())));

        checkProjectIsNotCreated("id", testData.getProject().getId());
    }

    @Test
    void creatingTwoProjectsWithSameNameInDifferentParentsShouldBeAvailable() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        var firstProject = checkedWithSuperUser.getProjectRequest().create(firstTestData.getProject());
        var secondProject = checkedWithSuperUser.getProjectRequest().create(secondTestData.getProject());

        var projectWithSameNameInSecondParent = TestDataGenerator.generateProject(secondProject.getId(),
                true);
        projectWithSameNameInSecondParent.setName(firstProject.getName());

        var project = checkedWithSuperUser.getProjectRequest().create(projectWithSameNameInSecondParent);

        checkProjectIsCreated(project);
        checkCreatedProjectData(project, projectWithSameNameInSecondParent,
                projectWithSameNameInSecondParent.getParentProject().getLocator());
    }

    @Test
    void creatingProjectWithNonUniqueIdShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        var projectWithSameId = testData.getProject();
        projectWithSameId.setName(RandomData.getString());

        uncheckedWithSuperUser.getProjectRequest().create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.NON_UNIQUE_PROJECT_ID.getText(),
                        testData.getProject().getId())));

        checkProjectIsNotCreated("name", projectWithSameId.getName());
    }

    @Test(dataProvider = "withoutCopySettings", dataProviderClass = ProjectsDataProvider.class)
    void creatingProjectWithoutCopyAllAssociatedSettingsShouldBeAvailable(Boolean value) {
        var testData = testDataStorage.addTestData();

        testData.getProject().setCopyAllAssociatedSettings(value);

        var project = checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        checkProjectIsCreated(project);
        checkCreatedProjectData(project, testData.getProject(), testData.getProject().getParentProject().getLocator());
        checkParentProject(project.getParentProject(), "_Root", null);
    }

    @Test
    void creatingProjectWithoutIdWillUseNameAsId() {
        var testData = testDataStorage.addTestData();

        testData.getProject().setId(null);

        var project = checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        softy.assertThat(project.getId())
                .isEqualToIgnoringCase(testData.getProject().getName().replace("_", ""));
        checkProjectIsCreated(project);
    }

    @Test
    void creatingProjectWithoutParentWillBeCreatedInRoot() {
        var testData = testDataStorage.addTestData();

        testData.getProject().setParentProject(null);

        var project = checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        checkProjectIsCreated(project);
        checkCreatedProjectData(project, testData.getProject(), "_Root");
        checkParentProject(project.getParentProject(), "_Root", null);
    }

    @Test
    void creatingProjectWithDeletedProjectsDataShouldBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());
        checkedWithSuperUser.getProjectRequest().delete(testData.getProject().getId());

        var project = checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        checkProjectIsCreated(project);
    }

    @Test
    void creatingProjectWithArchivedProjectsDataShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());
        archiveProject("true", testData.getProject().getId());

        uncheckedWithSuperUser.getProjectRequest().create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format(Errors.PROJECT_WITH_NAME_ALREADY_EXISTS.getText(),
                        testData.getProject().getName())));
    }

    //TODO Узнать maxLength для name и locator, тест проходит со значением 10000000
    @Test(dataProvider = "validName", dataProviderClass = BaseDataProvider.class)
    void creatingProjectWithValidNameAsLocatorShouldBeAvailable(String locator) {
        var parentTestData = testDataStorage.addTestData();
        var projectTestData = testDataStorage.addTestData();

        parentTestData.getProject().setName(locator);

        var parentProject = checkedWithSuperUser.getProjectRequest().create(parentTestData.getProject());

        projectTestData.getProject().getParentProject().setLocator(parentProject.getName());

        var project = checkedWithSuperUser.getProjectRequest().create(projectTestData.getProject());

        checkProjectIsCreated(project);
        checkCreatedProjectData(project, projectTestData.getProject(), parentTestData.getProject().getId());
        checkParentProject(project.getParentProject(), parentTestData.getProject().getId(),
                parentTestData.getProject().getName());
    }

    @Test(dataProvider = "validId", dataProviderClass = BaseDataProvider.class)
    void creatingProjectWithValidIdShouldBeAvailable(String id) {
        var testData = testDataStorage.addTestData();

        testData.getProject().setId(id);

        var project = checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        checkProjectIsCreated(project);
        checkCreatedProjectData(project, testData.getProject(), testData.getProject().getParentProject().getLocator());
    }

    @Test
    void creatingProjectWithXmlContentTypeShouldBeAvailable() throws JsonProcessingException {
        var testData = testDataStorage.addTestData();

        var projectXml = new XmlMapper().writeValueAsString(testData.getProject());

        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.XML);
        spec.setAccept(ContentType.XML);

        new UncheckedRequests(spec.build()).getProjectRequest().create(projectXml)
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test
    void creatingProjectWithNonExistedLocatorShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        testData.getProject().getParentProject().setLocator(RandomData.getString());

        uncheckedWithSuperUser.getProjectRequest().create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString(String.format(Errors.PROJECT_NOT_FOUND_BY_NAME_OR_ID.getText(),
                        testData.getProject().getParentProject().getLocator())));

        checkProjectIsNotCreated("id", testData.getProject().getId());
    }


    @Test(dataProvider = "invalidLocator", dataProviderClass = ProjectsDataProvider.class)
    void creatingProjectWithInvalidLocatorShouldNotBeAvailable(String locator, int code, String error) {
        var testData = testDataStorage.addTestData();

        testData.getProject().getParentProject().setLocator(locator);

        uncheckedWithSuperUser.getProjectRequest().create(testData.getProject())
                .then().assertThat().statusCode(code)
                .body(Matchers.containsString(error));

        checkProjectIsNotCreated("id", testData.getProject().getId());
    }

    /*
        Возможно, это by design, но при передаче пустой строки стоит тоже возвращать 400, а не 500.
        И текст ошибки как при отсутствии параметра, чтобы не плодить тексты ошибок с одинаковым смыслом
        (для id возращается одна и та же ошибка)
     */
    @Test(dataProvider = "invalidName", dataProviderClass = ProjectsDataProvider.class)
    void creatingProjectWithInvalidNameShouldNotBeAvailable(String name, String error) {
        var testData = testDataStorage.addTestData();

        testData.getProject().setName(name);

        uncheckedWithSuperUser.getProjectRequest().create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

        checkProjectIsNotCreated("id", testData.getProject().getId());
    }

    @Test(dataProvider = "invalidId", dataProviderClass = ProjectsDataProvider.class)
    void creatingProjectWithInvalidIdShouldNotBeAvailable(String id, String error) {
        var testData = testDataStorage.addTestData();

        testData.getProject().setId(id);

        uncheckedWithSuperUser.getProjectRequest().create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

        checkProjectIsNotCreated("name", testData.getProject().getName());
    }

    @Test
    void creatingProjectWithEmptyBodyShouldNotBeAvailable() {
        var testData = testDataStorage.addTestData();

        testData.setProject(NewProjectDescription.builder().build());

        uncheckedWithSuperUser.getProjectRequest().create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.EMPTY_PROJECT_NAME.getText()));
    }

    @Test
    void creatingProjectWithInvalidAcceptHeaderShouldNotBeAvailable() {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setAccept(ContentType.TEXT);

        new UncheckedRequests(spec.build()).getProjectRequest().create(RandomData.getString())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_ACCEPTABLE);
    }
}
