package com.example.teamcity.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectResponse {
    private String id;
    private String name;
    private String parentProjectId;
    private ParentProject parentProject;
    private BuildTypes buildTypes;
    private Projects projects;
}
