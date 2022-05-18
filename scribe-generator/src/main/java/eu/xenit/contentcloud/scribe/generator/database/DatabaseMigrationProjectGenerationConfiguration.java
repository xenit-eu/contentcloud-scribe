package eu.xenit.contentcloud.scribe.generator.database;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.database.operations.AggregateStatementGenerator;
import eu.xenit.contentcloud.scribe.generator.database.operations.AttributeOperationStatementGenerator;
import eu.xenit.contentcloud.scribe.generator.database.operations.EntityOperationStatementGenerator;
import eu.xenit.contentcloud.scribe.generator.database.operations.UnsupportedOperationErrorStatementGenerator;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class DatabaseMigrationProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    DatabaseMigrationProjectContributor databaseMigrationProjectContributor(
            DatabaseMigrationWriter databaseMigrationOperations) {
        return new DatabaseMigrationProjectContributor(description, databaseMigrationOperations);
    }

    @Bean
    DatabaseMigrationWriter databaseMigrationOperations() {
        return new DatabaseMigrationWriter(new UnsupportedOperationErrorStatementGenerator(new AggregateStatementGenerator(Set.of(
                new AttributeOperationStatementGenerator(),
                new EntityOperationStatementGenerator()
        ))));
    }

}
