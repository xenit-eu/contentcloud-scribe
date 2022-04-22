package eu.xenit.contentcloud.scribe.generator.repository;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.entitymodel.EntityModel;
import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import eu.xenit.contentcloud.scribe.generator.source.SourceGenerator;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class RepositoryProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    RepositoriesPoetSourceCodeProjectContributor repositoriesSourceCodeProjectContributor(EntityModel entityModel,
            SourceGenerator sourceGenerator) {
        return new RepositoriesPoetSourceCodeProjectContributor(this.description, entityModel, sourceGenerator);
    }

    @Bean
    ApplicationPropertiesCustomizer hibernateProperties() {
        return properties -> properties
                .put("spring.jpa.properties.hibernate.globally_quoted_identifiers", "true");
    }

}
