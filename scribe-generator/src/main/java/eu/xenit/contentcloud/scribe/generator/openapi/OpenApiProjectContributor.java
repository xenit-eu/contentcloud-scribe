package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenApiProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;
    private final EntityModel entityModel;

    private final OpenApiWriter openApiWriter = new OpenApiWriter();

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path openapiPath = projectRoot.resolve("openapi.yml");

        try (var writer = Files.newBufferedWriter(openapiPath)) {
            openApiWriter.writeOpenApiSpec(writer, this.entityModel);
        }
    }
}
