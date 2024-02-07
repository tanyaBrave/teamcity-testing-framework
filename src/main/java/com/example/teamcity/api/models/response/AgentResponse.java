package com.example.teamcity.api.models.response;

import com.example.teamcity.api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentResponse extends BaseModel {
    private String id;
    private List<Agent> agent;
}
