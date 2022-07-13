package eu.xenit.contentcloud.scribe.generator.opa;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class OpaPolicyProjectGenerationConfiguration {
    private final ScribeProjectDescription description;

    @Bean
    OpaPolicyProjectContributor opaPolicyProjectContributor(IndentingWriterFactory indentingWriterFactory) {
        return new OpaPolicyProjectContributor(description, indentingWriterFactory);
    }

}
