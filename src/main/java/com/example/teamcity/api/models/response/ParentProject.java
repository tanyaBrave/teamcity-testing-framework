package com.example.teamcity.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParentProject {
    private String id;
    private String name;
    private String description;
}
