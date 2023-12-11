package com.example.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "newProjectDescription")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {
    private String id;
    private String name;
    private String parentProjectId;
    @JacksonXmlProperty(isAttribute = true)
    private String locator;
}
