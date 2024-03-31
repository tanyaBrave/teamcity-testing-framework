package com.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Errors {
    PROJECT_NOT_FOUND_BY_NAME_OR_ID("No project found by name or internal/external id '%s'"),
    PROJECT_NOT_FOUND_BY_ID("No project found by locator 'count:1,id:%s'"),
    PROJECT_NOT_FOUND_BY_LOCATOR("No project found by locator 'count:1,id:%s'"),
    BUILD_CONFIG_NOT_FOUND_BY_ID("No build type nor template is found by id '%s'"),
    NOTHING_FOUND_BY_NAME("Nothing is found by locator 'count:1,name:%s'"),
    EMPTY_LOCATOR("No project specified. Either 'id', 'internalId' or 'locator' attribute should be present"),
    EMPTY_PROJECT_NAME("Project name cannot be empty"),
    EMPTY_GIVEN_PROJECT_NAME("Given project name is empty"),
    EMPTY_BUILD_CONFIG_NAME("When creating a build type, non empty name should be provided"),
    INVALID_PROJECT_ID_LENGTH("Project ID \"%s\" is invalid: it is 226 characters long while the maximum length "
            + "is 225."),
    INVALID_BUILD_CONFIG_ID_LENGTH("Build configuration or template ID \"%s\" is invalid: it is 226 characters "
            + "long while the maximum length is 225."),
    INVALID_PROJECT_ID_BEGINNING("Project ID \"%s\" is invalid: starts with non-letter character"),
    INVALID_BUILD_CONFIG_ID_BEGINNING("Build configuration or template ID \"%s\" is invalid: starts with "
            + "non-letter character"),
    INVALID_PROJECT_ID_LANGUAGE("Project ID \"%s\" is invalid: contains non-latin letter"),
    INVALID_BUILD_CONFIG_ID_LANGUAGE("Build configuration or template ID \"%s\" is invalid: contains "
            + "non-latin letter"),
    INVALID_PROJECT_ID_CHARACTERS("Project ID \"%s\" is invalid: contains unsupported character"),
    INVALID_BUILD_CONFIG_ID_CHARACTERS("Build configuration or template ID \"%s\" is invalid: contains "
            + "unsupported character"),
    EMPTY_PROJECT_ID("Project ID must not be empty"),
    EMPTY_BUILD_CONFIG_ID("Build configuration or template ID must not be empty"),
    PROJECT_WITH_NAME_ALREADY_EXISTS("Project with this name already exists: %s"),
    BUILD_CONFIG_WITH_NAME_ALREADY_EXISTS("Build configuration with name \"%s\" already exists in project"),
    NO_PERM_TO_CREATE_PROJECT("You do not have \"Create subproject\" permission in project with internal id"),
    NO_PERM_TO_EDIT_PROJECT("You do not have enough permissions to edit project with id: %s"),
    NON_UNIQUE_PROJECT_ID("Project ID \"%s\" is already used by another project"),
    NON_UNIQUE_BUILD_CONFIG_ID("The build configuration / template ID \"%s\" is already used by another "
            + "configuration or template"),
    BAD_LOCATOR_SYNTAX("Bad locator syntax: Invalid dimension name"),
    PROJECT_NOT_SPECIFIED("No project specified. Either 'id', 'internalId' or 'locator' attribute "
            + "should be present"),
    NULL_MY_BEAN_CONTEXT(" Cannot invoke \"jetbrains.buildServer.server.rest.util.BeanContext"
            + ".getSingletonService(java.lang.Class)\" because \"this.myBeanContext\" is null"),
    BUILD_TYPE_WITHOUT_PROJECT_NODE("Build type creation request should contain project node"),
    UNRECOGNIZED_URL("Cannot create a project using the specified URL. The URL is not recognized.");
    private String text;
}
