package eu.xenit.contentcloud.scribe.generator.repository;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.entitymodel.EntityModel;
import eu.xenit.contentcloud.scribe.generator.properties.ApplicationProperties;
import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.language.java.JavaSourceCodeWriter;
import io.spring.initializr.generator.language.java.ScribeJavaSourceCodeWriter;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class RepositoriesGenerationConfiguration {

    private final ScribeProjectDescription description;

    private final IndentingWriterFactory indentingWriterFactory;

    @Bean
    RepositoriesPoetSourceCodeProjectContributor repositoriesSourceCodeProjectContributor(EntityModel entityModel) {
        return new RepositoriesPoetSourceCodeProjectContributor(this.description, entityModel);
    }

    @Bean
    ApplicationPropertiesCustomizer hibernateProperties() {
        return properties -> properties
                .put("spring.jpa.properties.hibernate.globally_quoted_identifiers", "true");
    }

}
