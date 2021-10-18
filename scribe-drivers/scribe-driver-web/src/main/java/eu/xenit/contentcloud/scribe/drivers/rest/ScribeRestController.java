package eu.xenit.contentcloud.scribe.drivers.rest;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;

import java.util.Map;

public class ScribeRestController extends ProjectGenerationController<ScribeProjectRequest> {

    public ScribeRestController(InitializrMetadataProvider metadataProvider, ProjectGenerationInvoker<ScribeProjectRequest> projectGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);
    }

    @Override
    public ScribeProjectRequest projectRequest(Map<String, String> headers) {

        ScribeProjectRequest request = new ScribeProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());

        return request;
    }
}

