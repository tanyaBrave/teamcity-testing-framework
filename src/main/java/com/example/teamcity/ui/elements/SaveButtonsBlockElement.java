package com.example.teamcity.ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.element;

public class SaveButtonsBlockElement extends PageElement {
    private final SelenideElement savingWaitingMarker;
    private final SelenideElement submitButton;

    public SaveButtonsBlockElement() {
        super(element(Selectors.byClass("saveButtonsBlock")));
        this.submitButton = element(Selectors.byType("submit"));
        this.savingWaitingMarker = element(Selectors.byId("saving"));
    }

    public void submit() {
        submitButton.click();
        savingWaitingMarker.shouldNotBe(Condition.visible, Duration.ofSeconds(30));
    }
}
