package eu.xenit.contentcloud.scribe.drivers.rest;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;

import java.util.Map;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

public class ScribeRestController extends ProjectGenerationController<ScribeProjectRequest> {

    @NonNull
    private final OpenApiGenerationInvoker<ScribeProjectRequest> openApiGenerationInvoker;

    public ScribeRestController(InitializrMetadataProvider metadataProvider,
            ProjectGenerationInvoker<ScribeProjectRequest> projectGenerationInvoker,
            OpenApiGenerationInvoker<ScribeProjectRequest> openApiGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);

        this.openApiGenerationInvoker = openApiGenerationInvoker;
    }

    @Override
    public ScribeProjectRequest projectRequest(Map<String, String> headers) {

        ScribeProjectRequest request = new ScribeProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());

        return request;
    }

    @RequestMapping(path = { "/openapi", "/openapi.yml" })
    public ResponseEntity<byte[]> gradle(ScribeProjectRequest request) {

        byte[] openApiSpec = this.openApiGenerationInvoker.invokeOpenapiGeneration(request);
        return createResponseEntity(openApiSpec, "application/openapi+yaml", "openapi.yml");
    }

    private ResponseEntity<byte[]> createResponseEntity(byte[] content, String contentType, String fileName) {
        String contentDispositionValue = "attachment; filename=\"" + fileName + "\"";
        return ResponseEntity.ok().header("Content-Type", contentType)
                .header("Content-Disposition", contentDispositionValue).body(content);
    }
}

