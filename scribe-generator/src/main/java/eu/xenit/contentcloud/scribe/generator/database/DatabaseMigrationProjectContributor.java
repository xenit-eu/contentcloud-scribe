package eu.xenit.contentcloud.scribe.generator.database;

import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DatabaseMigrationProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;

    @Override
    public void contribute(Path projectRoot) throws IOException {
        SourceStructure mainSource = description.getBuildSystem().getMainSource(projectRoot, description.getLanguage());
        Path resourcesDir = projectRoot.resolve(mainSource.getResourcesDirectory());
        Path migrationsDir = Files.createDirectories(resourcesDir.resolve("db/migration"));


        var changeset = description.getChangeset();
        this.contributeChangesetMigration(changeset, migrationsDir);
    }

    private void contributeChangesetMigration(Changeset changeset, Path migrationsDir) throws IOException {


        Path sql = Files.createFile(migrationsDir.resolve("V1.sql"));
        try (var writer = Files.newBufferedWriter(sql)) {
            this.writeMigrationSql(writer, changeset);
        }
    }

    void writeMigrationSql(Writer out, Changeset changeset) throws IOException {
        for (var operation : changeset.getOperations()) {
            this.writeMigrationOperation(out, operation);
        }
    }


    void writeMigrationOperation(Writer out, Operation operation) {
        var handler = DatabaseMigrationOperations.OPERATIONS.get(operation.getType());
        if (handler == null) {
            log.warn("Unhandled operation type: {}", operation.getType());
        } else {
            handler.accept(out, operation);
        }
    }
}
