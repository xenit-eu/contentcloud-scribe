package eu.xenit.contentcloud.scribe.generator.database;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.database.operations.AttributeOperationWriter;
import eu.xenit.contentcloud.scribe.generator.database.operations.EntityOperationWriter;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class DatabaseMigrationProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    DatabaseMigrationProjectContributor databaseMigrationProjectContributor(DatabaseMigrationOperations databaseMigrationOperations) {
        return new DatabaseMigrationProjectContributor(description, databaseMigrationOperations);
    }

    @Bean
    DatabaseMigrationOperations databaseMigrationOperations() {
        return new DatabaseMigrationOperations(Set.of(
                new AttributeOperationWriter(),
                new EntityOperationWriter()
        ));
    }

}
