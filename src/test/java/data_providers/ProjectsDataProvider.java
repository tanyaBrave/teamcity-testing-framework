package data_providers;

import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.generators.RandomData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;

public class ProjectsDataProvider {

    @DataProvider
    public static Object[][] withoutCopySettings() {
        return new Object[][] {
                {false},
                {null}
        };
    }

    @DataProvider
    public static Object[][] invalidLocator() {
        return new Object[][] {
                {"", HttpStatus.SC_BAD_REQUEST, Errors.EMPTY_LOCATOR.getText()},
                {" ", HttpStatus.SC_NOT_FOUND, String.format(Errors.PROJECT_NOT_FOUND_BY_NAME_OR_ID.getText(), " ")},
                {null, HttpStatus.SC_BAD_REQUEST, Errors.EMPTY_LOCATOR.getText()}
        };
    }

    @DataProvider
    public static Object[][] invalidName() {
        return new Object[][] {
                {" ", Errors.EMPTY_GIVEN_PROJECT_NAME.getText()},
                {"", Errors.EMPTY_PROJECT_NAME.getText()},
                {null, Errors.EMPTY_PROJECT_NAME.getText()}
        };
    }

    @DataProvider
    public static Object[][] invalidId() {
        String id = RandomData.getString();
        String tooLongString = RandomStringUtils.randomAlphabetic(226);
        String cyrillicString = RandomData.getCyrillicString();
        String numericString = RandomStringUtils.randomNumeric(10);
        String specialCharactersString = id + RandomData.getSpecialCharactersString();
        return new Object[][] {
                {tooLongString, String.format(Errors.INVALID_PROJECT_ID_LENGTH.getText(), tooLongString)},
                {"_" + id, String.format(Errors.INVALID_PROJECT_ID_BEGINNING.getText(), "_" + id)},
                {cyrillicString, String.format(Errors.INVALID_PROJECT_ID_LANGUAGE.getText(), cyrillicString)},
                {specialCharactersString, String.format(Errors.INVALID_PROJECT_ID_CHARACTERS.getText(),
                        specialCharactersString)},
                {" ", Errors.EMPTY_PROJECT_ID.getText()},
                {"", Errors.EMPTY_PROJECT_ID.getText()},
                {" " + id, String.format(Errors.INVALID_PROJECT_ID_BEGINNING.getText(), " " + id)},
                {id + " " + id, String.format(Errors.INVALID_PROJECT_ID_CHARACTERS.getText(), id + " " + id)},
                {id + " ", String.format(Errors.INVALID_PROJECT_ID_CHARACTERS.getText(), id + " ")},
                {numericString, String.format(Errors.INVALID_PROJECT_ID_BEGINNING.getText(), numericString)}
        };
    }
}
