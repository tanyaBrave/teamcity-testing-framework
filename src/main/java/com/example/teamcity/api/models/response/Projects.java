package com.example.teamcity.api.models.response;

import com.example.teamcity.api.models.Project;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Projects {
    private List<Project> project;
}
