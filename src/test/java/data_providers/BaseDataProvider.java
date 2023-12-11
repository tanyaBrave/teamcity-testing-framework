package data_providers;

import com.example.teamcity.api.generators.RandomData;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;

public class BaseDataProvider {

    @DataProvider
    public static Object[][] validId() {
        return new Object[][] {
                {RandomStringUtils.randomAlphabetic(1)},
                {RandomStringUtils.randomAlphabetic(225)},
                {RandomData.getString() + RandomStringUtils.randomNumeric(10)}
        };
    }

    @DataProvider
    public static Object[][] validName() {
        return new Object[][] {
                {RandomStringUtils.randomAlphabetic(1)},
                {RandomStringUtils.randomAlphabetic(10000000)},
                {" " + RandomData.getString()},
                {RandomData.getString() + " " + RandomData.getString()},
                {RandomData.getString() + " "},
                {RandomData.getCyrillicString()},
                {RandomData.getSpecialCharactersString()},
                {RandomStringUtils.randomNumeric(10)}
        };
    }
}
