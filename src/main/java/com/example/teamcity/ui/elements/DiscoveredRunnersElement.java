package com.example.teamcity.ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.element;
import static com.codeborne.selenide.Selenide.elements;

public class DiscoveredRunnersElement extends PageElement {

    private final SelenideElement parametersTable;
    private final ElementsCollection checkboxes;

    public DiscoveredRunnersElement() {
        super(element(Selectors.byId("discoveredRunners")));
        this.parametersTable = element(Selectors.byClass("parametersTable"));
        this.checkboxes = elements(Selectors.byId("runnerId"));
    }

    public void selectAll() {
        parametersTable.shouldHave(Condition.visible, Duration.ofSeconds(20));
        checkboxes.forEach(SelenideElement::click);
    }
}
