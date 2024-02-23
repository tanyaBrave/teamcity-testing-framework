package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public abstract class PageElement {

    private final SelenideElement element;

    public PageElement(SelenideElement element) {
        this.element = element;
    }

    /**
     * Поиск элемента
     * @param by - тип локатора для поиска
     * @return найденный элемент
     */
    public SelenideElement findElement(By by) {
        return element.find(by);
    }

    /**
     * оиск элемента
     * @param value - строка с каким-то значением для поиска
     * @return найденный элемент
     */
    public SelenideElement findElement(String value) {
        return element.find(value);
    }
}
