package eu.xenit.contentcloud.scribe.generator.repository;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.entitymodel.EntityModel;
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
    public RepositoriesSourceCodeProjectContributor repositoriesSourceCodeProjectContributor(EntityModel entityModel) {
        return new RepositoriesSourceCodeProjectContributor(this.description, entityModel, JavaSourceCode::new,
                new ScribeJavaSourceCodeWriter(this.indentingWriterFactory));
    }

}
