package com.example.teamcity.api.enums;

import lombok.Getter;

@Getter
public enum Role {
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    PROJECT_ADMIN("PROJECT_ADMIN"),
    PROJECT_DEVELOPER("PROJECT_DEVELOPER"),
    PROJECT_VIEWER("PROJECT_VIEWER"),
    AGENT_MANAGER("AGENT_MANAGER");

    private final String text;

    Role(String text) {
        this.text = text;
    }
}
