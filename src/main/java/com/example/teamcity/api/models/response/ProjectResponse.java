package com.example.teamcity.api.models.response;

import com.example.teamcity.api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectResponse extends BaseModel {
    private String id;
    private String name;
    private String parentProjectId;
    private ParentProject parentProject;
    private BuildTypes buildTypes;
    private Projects projects;
}
