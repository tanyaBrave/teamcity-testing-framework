package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.elements.PageElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.element;
import static com.codeborne.selenide.Selenide.elements;

public abstract class Page {

    private SelenideElement submitButton = element(Selectors.byType("submit"));
    private SelenideElement savingWaitingMarker = element(Selectors.byId("saving"));
    private SelenideElement pageWaitingMarker = element(Selectors.byDataTest("ring-loader"));
    private ElementsCollection errors = elements(Selectors.byClass("error"));
    protected SelenideElement urlInput = element(Selectors.byId("url"));
    protected SelenideElement buildTypeNameInput = element(Selectors.byId("buildTypeName"));

    /**
     * Нажатие на кнопку подтверждения
     */
    public void submit() {
        submitButton.click();
        waitUntilDataIsSaved();
    }

    /**
     * Ожидание полной загрузки страницы
     */
    public void waitUntilPageIsLoaded() {
        pageWaitingMarker.shouldNotBe(Condition.visible, Duration.ofMinutes(1));
    }

    /**
     * Ожидание сохранения данных на странице
     */
    public void waitUntilDataIsSaved() {
        savingWaitingMarker.shouldNotBe(Condition.visible, Duration.ofSeconds(30));
    }

    /**
     * Ожидание видимости элемента
     */
    public void waitUntilElementVisible(SelenideElement element) {
        element.shouldBe(Condition.visible, Duration.ofSeconds(30));
    }

    /**
     * @param collection - коллекция элементов, которую ожидается найти на странице
     * @param creator - объект класса Function, который используется создания элемента
     * @return коллекцию созданных PageElement
     * @param <T> - класс наследник PageElement
     */
    protected <T extends PageElement> List<T> generatePageElements(ElementsCollection collection,
                                                                   Function<SelenideElement, T> creator) {
        var elements = new ArrayList<T>();
        collection.forEach(webElement -> elements.add(creator.apply(webElement)));
        return elements;
    }

    /**
     * @param id - элемента содержащего ошибку
     * @param errorText - ожидаемый текст ошибки
     */
    public void checkErrorText(String id, String errorText) {
        errors.findBy(Condition.id("error_" + id)).shouldHave(Condition.text(errorText));
    }
}
