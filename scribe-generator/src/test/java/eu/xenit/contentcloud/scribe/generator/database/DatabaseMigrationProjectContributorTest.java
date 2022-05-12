package eu.xenit.contentcloud.scribe.generator.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.content.SpringContentProjectionGenerationConfiguration;
import eu.xenit.contentcloud.scribe.generator.spring.data.SpringDataProjectGenerationConfiguration;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.code.java.JavaProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseMigrationProjectContributorTest {

    private ProjectAssetTester projectTester;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester()
                .withIndentingWriterFactory()
                .withConfiguration(DatabaseMigrationProjectGenerationConfiguration.class)
                .withDirectory(directory)
                .withDescriptionCustomizer((description) -> {
                    description.setLanguage(new JavaLanguage());
                    description.setPlatformVersion(Version.parse("2.6.6"));
                    description.setBuildSystem(new GradleBuildSystem());
                });
    }

    @Test
    void contribute() {

        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of())
                .operations(List.of(
                        new Operation("add-entity", Map.of(
                                "entity-name", "Invoice",
                                "entity-id", "inv001"
                        )),
                        new Operation("add-attribute", Map.of(
                                "entity-name", "Invoice",
                                "attribute-name", "Amount",
                                "type", "LONG",
                                "attribute-id", "inv001amo001",
                                "naturalId", false,
                                "indexed", true,
                                "unique", false,
                                "required", false
                        )),
                        new Operation("add-attribute", Map.of(
                                "entity-name", "Invoice",
                                "attribute-name", "Reference",
                                "type", "STRING",
                                "attribute-id", "inv001ref001",
                                "naturalId", true,
                                "indexed", true,
                                "unique", true,
                                "required", true
                        ))
                        /*
                        new Operation("add-attribute", Map.of(
                                "entity-name", "Invoice",
                                "attribute-name", "Scan",
                                "type", "CONTENT",
                                "attribute-id", "inv001sca001",
                                "naturalId", false,
                                "indexed", false,
                                "unique", false,
                                "required", true
                        ))*/
                ))
                .build());

        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/resources/db/migration/V1.sql";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path)
                .containsExactly(
                        "CREATE TABLE \"invoice\" (id UUID PRIMARY KEY);",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"amount\" bigint NULL;",
                        "CREATE INDEX CONCURRENTLY \"inv001amo001_idx\" ON \"invoice\"(\"amount\");",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"reference\" text NOT NULL;",
                        "CREATE UNIQUE INDEX CONCURRENTLY \"inv001ref001_uniq\" ON \"invoice\"(\"reference\");",
                        "CREATE INDEX CONCURRENTLY \"inv001ref001_idx\" ON \"invoice\"(\"reference\");"
                );


    }
}