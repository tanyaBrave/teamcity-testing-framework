package data_providers;

import com.example.teamcity.api.generators.RandomData;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;

import static com.example.teamcity.api.constants.DataLimits.ID_MAX_LIMIT;
import static com.example.teamcity.api.constants.DataLimits.ID_MIN_LIMIT;
import static com.example.teamcity.api.constants.DataLimits.NAME_MAX_LIMIT;
import static com.example.teamcity.api.constants.DataLimits.NAME_MIN_LIMIT;

public class BaseDataProvider {

    @DataProvider
    public static Object[][] validId() {
        return new Object[][] {
                {RandomStringUtils.randomAlphabetic(ID_MIN_LIMIT)},
                {RandomStringUtils.randomAlphabetic(ID_MAX_LIMIT)},
                {RandomData.getString() + RandomData.getNumericString()}
        };
    }

    @DataProvider
    public static Object[][] validName() {
        return new Object[][] {
                {RandomStringUtils.randomAlphabetic(NAME_MIN_LIMIT)},
                {RandomStringUtils.randomAlphabetic(NAME_MAX_LIMIT)},
                {" " + RandomData.getString()},
                {RandomData.getString() + " " + RandomData.getString()},
                {RandomData.getString() + " "},
                {RandomData.getCyrillicString()},
                {RandomData.getSpecialCharactersString()},
                {RandomData.getNumericString()}
        };
    }
}
