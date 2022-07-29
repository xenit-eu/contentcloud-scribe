package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntityModel;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class OpenApiProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    OpenApiProjectContributor openApiProjectContributor(RestResourceEntityModel entityModel) {
        return new OpenApiProjectContributor(description, entityModel);
    }


}
