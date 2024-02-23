package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import lombok.Getter;

@Getter
public class BuildElement extends PageElement {

    private final SelenideElement title;

    public BuildElement(SelenideElement element) {
        super(element);
        this.title = findElement(Selectors.byDataTest("ring-link"));
    }
}
