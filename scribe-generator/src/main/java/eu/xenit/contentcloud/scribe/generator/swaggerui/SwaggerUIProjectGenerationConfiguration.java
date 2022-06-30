package eu.xenit.contentcloud.scribe.generator.swaggerui;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class SwaggerUIProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    SwaggerUIDependencyBuildCustomizer swaggerUIDependencyBuildCustomizer(InitializrMetadata metadata) {
        return new SwaggerUIDependencyBuildCustomizer(this.description, metadata);
    }
}
