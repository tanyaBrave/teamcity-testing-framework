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
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Issues;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.qameta.allure.testng.Tag;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.MINOR;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static io.qameta.allure.SeverityLevel.TRIVIAL;

@Epic("API tests")
@Feature("TeamCity project")
@Story("Creating project")
@Tag("Regression")
public class ProjectTest extends BaseApiTest {

    @Test(groups = {"Regression"})
    @Description("Creating project in Root project should be available")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-26")
    void creatingProjectInRootShouldBeAvailable() {
        var project = createProject(superUserSpec, testData.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, testData.getProject(), testData.getProject().getParentProject().getLocator());
        checkParentProject(project.getParentProject(), "_Root", null);
    }

    @Test(groups = {"Regression"})
    @Description("Creating project not in Root project should be available")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-27")
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
    @Description("Creating project with name as locator should be available")
    @Severity(CRITICAL)
    @TmsLink("TC-TMS-28")
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
    @Description("Creating two projects with same name should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-29")
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
    @Description("Creating two projects with same name in different parent projects should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-30")
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
    @Description("Creating project with non unique should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-31")
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
    @Description("Creating project without copy all associated settings should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-32")
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
    @Description("When create project without id it's name will be used as id")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-33")
    void creatingProjectWithoutIdWillUseNameAsId() {
        var testDataWithNullId = testData;

        testDataWithNullId.getProject().setId(null);

        var project = createProject(superUserSpec, testDataWithNullId.getProject());

        softy.assertThat(project.getId())
                .isEqualToIgnoringCase(testDataWithNullId.getProject().getName().replace("_", ""));
        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
    }

    @Test(groups = {"Regression"})
    @Description("Creating project without specifying parent will be create in Root")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-34")
    void creatingProjectWithoutParentWillBeCreatedInRoot() {
        var testDataWithNullParentProject = testData;

        testDataWithNullParentProject.getProject().setParentProject(null);

        var project = createProject(superUserSpec, testDataWithNullParentProject.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, testDataWithNullParentProject.getProject(), "_Root");
        checkParentProject(project.getParentProject(), "_Root", null);
    }

    @Test(groups = {"Regression"})
    @Description("Creating project with deleted project data should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-35")
    void creatingProjectWithDeletedProjectsDataShouldBeAvailable() {
        createProject(superUserSpec, testData.getProject());
        checkedWithSuperUser.getRequest(Endpoint.PROJECTS).delete("/id:" + testData.getProject().getId());

        var project = createProject(superUserSpec, testData.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
    }

    @Test(groups = {"Regression"})
    @Description("Creating project with archived project data should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-36")
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
    @Description("Creating project with valid locator name should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-37")
    @Issue("TC-IMPROVEMENT-2")
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
    @Description("Creating project with valid id should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-38")
    void creatingProjectWithValidIdShouldBeAvailable(String id) {
        var testDataWithValidId = testData;

        testDataWithValidId.getProject().setId(id);

        var project = createProject(superUserSpec, testDataWithValidId.getProject());

        checkProjectIsCreated(superUserSpec, "/id:" + project.getId());
        checkCreatedProjectData(project, testDataWithValidId.getProject(), testDataWithValidId.getProject().getParentProject().getLocator());
    }

    @Test(groups = {"Regression"})
    @Description("Creating project with xml content-type should be available")
    @Severity(NORMAL)
    @TmsLink("TC-TMS-39")
    void creatingProjectWithXmlContentTypeShouldBeAvailable() throws JsonProcessingException {

        var projectXml = new XmlMapper().writeValueAsString(testData.getProject());

        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setContentType(ContentType.XML);
        spec.setAccept(ContentType.XML);

        new UncheckedRequests(spec.build()).getRequest(Endpoint.PROJECTS).create(projectXml)
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test(groups = {"Regression"})
    @Description("Creating project with non existed locator should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-40")
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
    @Description("Creating project with invalid locator should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-41")
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
    @Description("Creating project with invalid name should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-42")
    @Issues({@Issue("TC-IMPROVEMENT-3"), @Issue("TC-BUGS-4")})
    void creatingProjectWithInvalidNameShouldNotBeAvailable(String name, String error) {
        var testDataWithInvalidName = testData;

        testDataWithInvalidName.getProject().setName(name);

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testDataWithInvalidName.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "id", testDataWithInvalidName.getProject().getId());
    }

    @Test(groups = {"Regression"}, dataProvider = "invalidId", dataProviderClass = ProjectsDataProvider.class)
    @Description("Creating project with invalid id should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-43")
    @Issue("TC-BUGS-3")
    void creatingProjectWithInvalidIdShouldNotBeAvailable(String id, String error) {
        var testDataWithInvalidId = testData;

        testDataWithInvalidId.getProject().setId(id);

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testDataWithInvalidId.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(error));

        checkProjectIsNotCreated(uncheckedWithSuperUser, "name", testDataWithInvalidId.getProject().getName());
    }

    @Test(groups = {"Regression"})
    @Description("Creating project with empty body should not be available")
    @Severity(TRIVIAL)
    @TmsLink("TC-TMS-44")
    void creatingProjectWithEmptyBodyShouldNotBeAvailable() {
        var testDataWithEmptyProject = testData;

        testDataWithEmptyProject.setProject(NewProjectDescription.builder().build());

        uncheckedWithSuperUser.getRequest(Endpoint.PROJECTS).create(testDataWithEmptyProject.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(Errors.EMPTY_PROJECT_NAME.getText()));
    }

    @Test(groups = {"Regression"})
    @Description("Creating project with invalid Accept header should not be available")
    @Severity(MINOR)
    @TmsLink("TC-TMS-45")
    void creatingProjectWithInvalidAcceptHeaderShouldNotBeAvailable() {
        var spec = Specifications.getSpec().superUserSpecBuilder();
        spec.setAccept(ContentType.TEXT);

        new UncheckedRequests(spec.build()).getRequest(Endpoint.PROJECTS).create(RandomData.getString())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_ACCEPTABLE);
    }
}
