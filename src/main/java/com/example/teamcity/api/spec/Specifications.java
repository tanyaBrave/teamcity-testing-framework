package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.User;
import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.nio.file.Paths;

public class Specifications {

    private static Specifications spec;

    private Specifications() {

    }

    public static Specifications getSpec() {
        if (spec == null) {
            spec = new Specifications();
        }
        return spec;
    }

    private RequestSpecBuilder reqBuilder() {
        var requestBuilder = new RequestSpecBuilder();
        requestBuilder.setBaseUri("http://" + Config.getProperty("host"));
        requestBuilder.addFilter(new RequestLoggingFilter());
        requestBuilder.addFilter(new ResponseLoggingFilter());
        requestBuilder.addFilter(new SwaggerCoverageRestAssured(new FileSystemOutputWriter(
                Paths.get("target/swagger-coverage-output"))));
        requestBuilder.setContentType(ContentType.JSON);
        requestBuilder.setAccept(ContentType.JSON);
        return requestBuilder;
    }

    public RequestSpecification unAuthSpec() {
        var requestBuilder = reqBuilder();
        return requestBuilder.build();
    }

    public RequestSpecification authSpec(User user) {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://" + user.getUsername() + ":" + user.getPassword() + "@"
                + Config.getProperty("host"));
        return requestBuilder.build();
    }

    public RequestSpecification superUserSpec() {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://:" + Config.getProperty("superUserToken") + "@" + Config.getProperty("host"));
        return requestBuilder.build();
    }

    public RequestSpecBuilder superUserSpecBuilder() {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://:" + Config.getProperty("superUserToken") + "@" + Config.getProperty("host"));
        return requestBuilder;
    }
}
