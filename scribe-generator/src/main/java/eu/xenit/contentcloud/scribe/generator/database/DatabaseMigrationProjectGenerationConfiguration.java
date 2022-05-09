package eu.xenit.contentcloud.scribe.generator.database;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class DatabaseMigrationProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    DatabaseMigrationProjectContributor databaseMigrationProjectContributor() {
        return new DatabaseMigrationProjectContributor(description);
    }

}
