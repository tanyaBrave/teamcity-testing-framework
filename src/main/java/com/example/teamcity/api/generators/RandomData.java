package com.example.teamcity.api.generators;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class RandomData {

    public static final int LENGTH = 10;
    public static final Locale RU = new Locale("ru", "RU");

    public static String getString() {
        return "test_" + RandomStringUtils.randomAlphabetic(LENGTH);
    }

    public static String getNumericString() {
        return "test_" + RandomStringUtils.randomNumeric(LENGTH);
    }
    public static String getCyrillicString() {
        return new Faker(RU).regexify("[А-Яа-яЁё]{" + LENGTH + "}");
    }

    public static String getSpecialCharactersString() {
        return new Faker().regexify("[.,!<>\\/\"\"''`=+-()*]{30}");
    }

    private RandomData() {

    }
}
