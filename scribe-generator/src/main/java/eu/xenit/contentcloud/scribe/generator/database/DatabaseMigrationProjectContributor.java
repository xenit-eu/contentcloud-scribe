package eu.xenit.contentcloud.scribe.generator.database;

import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatabaseMigrationProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;
    private final DatabaseMigrationWriter migrationOperations;

    @Override
    public void contribute(Path projectRoot) throws IOException {
        SourceStructure mainSource = description.getBuildSystem().getMainSource(projectRoot, description.getLanguage());
        Path resourcesDir = projectRoot.resolve(mainSource.getResourcesDirectory());
        Path migrationsDir = Files.createDirectories(resourcesDir.resolve("db/migration"));

        var head = description.getChangeset();
        Deque<Changeset> changesets = new LinkedList<>();

        for(var parent = Optional.ofNullable(head); parent.isPresent(); parent = parent.get().getParent()) {
            changesets.addFirst(parent.get());
        }

        int i = 0;
        for(Changeset changeset: changesets) {
            i++;
            this.contributeChangesetMigration(changeset, migrationsDir, i);
        }
    }

    private void contributeChangesetMigration(Changeset changeset, Path migrationsDir, int version) throws IOException {
        Path sql = Files.createFile(migrationsDir.resolve("V"+version+".sql"));
        try (var writer = Files.newBufferedWriter(sql)) {
            for (var operation : changeset.getOperations()) {
                migrationOperations.writeOperation(writer, operation);
            }
        }
    }

}
