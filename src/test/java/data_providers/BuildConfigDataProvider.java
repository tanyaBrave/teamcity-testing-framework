package data_providers;

import com.example.teamcity.api.enums.Errors;
import com.example.teamcity.api.generators.RandomData;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;

public class BuildConfigDataProvider {

    @DataProvider
    public static Object[][] invalidId() {
        String id = RandomData.getString();
        String tooLongString = RandomStringUtils.randomAlphabetic(226);
        String cyrillicString = RandomData.getCyrillicString();
        String numericString = RandomStringUtils.randomNumeric(10);
        String specialCharactersString = id + RandomData.getSpecialCharactersString();
        return new Object[][]{
                {tooLongString, String.format(Errors.INVALID_BUILD_CONFIG_ID_LENGTH.getText(), tooLongString)},
                {"_" + id, String.format(Errors.INVALID_BUILD_CONFIG_ID_BEGINNING.getText(), "_" + id)},
                {cyrillicString, String.format(Errors.INVALID_BUILD_CONFIG_ID_LANGUAGE.getText(), cyrillicString)},
                {specialCharactersString, String.format(Errors.INVALID_BUILD_CONFIG_ID_CHARACTERS.getText(),
                        specialCharactersString)},
                {" ", Errors.EMPTY_BUILD_CONFIG_ID.getText()},
                {"", Errors.EMPTY_BUILD_CONFIG_ID.getText()},
                {" " + id, String.format(Errors.INVALID_BUILD_CONFIG_ID_BEGINNING.getText(), " " + id)},
                {id + " " + id, String.format(Errors.INVALID_BUILD_CONFIG_ID_CHARACTERS.getText(), id + " " + id)},
                {id + " ", String.format(Errors.INVALID_BUILD_CONFIG_ID_CHARACTERS.getText(), id + " ")},
                {numericString, String.format(Errors.INVALID_BUILD_CONFIG_ID_BEGINNING.getText(), numericString)}
        };
    }

    @DataProvider
    public static Object[][] invalidName() {
        return new Object[][] {
                {" "},
                {""},
                {null}
        };
    }

    @DataProvider
    public static Object[][] invalidProjectId() {
        String id = RandomData.getString();
        String tooLongString = RandomStringUtils.randomAlphabetic(226);
        String cyrillicString = RandomData.getCyrillicString();
        String numericString = RandomStringUtils.randomNumeric(10);
        return new Object[][] {
                {tooLongString, String.format(Errors.INVALID_PROJECT_ID_LENGTH.getText(), tooLongString)},
                {"_" + id, String.format(Errors.INVALID_PROJECT_ID_BEGINNING.getText(), "_" + id)},
                {cyrillicString, String.format(Errors.INVALID_PROJECT_ID_LANGUAGE.getText(), cyrillicString)},
                {id + RandomData.getSpecialCharactersString(), Errors.BAD_LOCATOR_SYNTAX.getText()},
                {" ", Errors.EMPTY_PROJECT_ID.getText()},
                {"", Errors.EMPTY_PROJECT_ID.getText()},
                {" " + id, String.format(Errors.INVALID_PROJECT_ID_BEGINNING.getText(), " " + id)},
                {id + " " + id, String.format(Errors.INVALID_PROJECT_ID_CHARACTERS.getText(), id + " " + id)},
                {id + " ", String.format(Errors.INVALID_PROJECT_ID_CHARACTERS.getText(), id + " ")},
                {numericString, String.format(Errors.INVALID_PROJECT_ID_BEGINNING.getText(), numericString)},
                {null, Errors.PROJECT_NOT_SPECIFIED.getText()}
        };
    }
}
