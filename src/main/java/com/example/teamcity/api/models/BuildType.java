package com.example.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "buildType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildType extends BaseModel {
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    private NewProjectDescription project;
    private Steps steps;
    private String projectId;
    private String projectName;
}
