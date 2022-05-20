package eu.xenit.contentcloud.scribe.generator.database;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Model;
import eu.xenit.contentcloud.scribe.changeset.Relation;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.ChangesetFactory;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ChangesetDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ProjectDto;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;

class DatabaseMigrationProjectContributorTest {

    private ProjectAssetTester projectTester;
    private ObjectMapper objectMapper;

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
        this.objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    void contributeSingle() {

        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .baseModel(new Model(Collections.emptyList()))
                .operation("add-entity", Map.of(
                                "entity-name", "Invoice"
                        ),
                        new Model(List.of(Entity.builder()
                                .name("Invoice")
                                .build()))
                )
                .operation("add-attribute", Map.of(
                                "entity-name", "Invoice",
                                "attribute-name", "Amount",
                                "type", "LONG",
                                "naturalId", false,
                                "indexed", true,
                                "unique", false,
                                "required", false
                        ),
                        new Model(List.of(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .build()))
                )
                .operation("add-attribute", Map.of(
                                "entity-name", "Invoice",
                                "attribute-name", "Reference",
                                "type", "STRING",
                                "naturalId", true,
                                "indexed", true,
                                "unique", true,
                                "required", true
                        ), new Model(List.of(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Reference").string().unique(true).required(true).naturalId(true).indexed(true).build())
                                .build()))
                )
                .operation("add-attribute", Map.of(
                        "entity-name", "Invoice",
                        "attribute-name", "Scan",
                        "type", "CONTENT",
                        "naturalId", false,
                        "indexed", false,
                        "unique", false,
                        "required", true
                ), Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Reference").string().unique(true).required(true).naturalId(true).indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().required(true).build())
                                .build())
                        .build())
                .operation("add-entity", Map.of(
                        "entity-name", "Party"
                ), Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Reference").string().unique(true).required(true).naturalId(true).indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().required(true).build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build())
                .operation("add-relation", Map.of(
                        "source-entity", "Invoice",
                        "target-entity", "Party",
                        "relation-name", "sentBy",
                        "cardinality", "MANY_TO_ONE",
                        "required", true
                ), Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Reference").string().unique(true).required(true).naturalId(true).indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().required(true).build())
                                .relation(Relation.builder().name("sentBy").source("Invoice").target("Party").manySourcePerTarget(true).required(true).build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build())
                .build());

        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/resources/db/migration/V1.sql";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path)
                .lines()
                .filteredOn(line -> !line.startsWith("--"))
                .containsExactly(
                        "CREATE TABLE \"invoice\" (id UUID PRIMARY KEY);",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"amount\" bigint NULL;",
                        "CREATE INDEX CONCURRENTLY \"invoice__amount_idx\" ON \"invoice\"(\"amount\");",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"reference\" text NOT NULL;",
                        "CREATE UNIQUE INDEX CONCURRENTLY \"invoice__reference_idx\" ON \"invoice\"(\"reference\");",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"scan_id\" text NOT NULL;",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"scan_length\" bigint NOT NULL;",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"scan_mimetype\" text NOT NULL;",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"scan_filename\" text NOT NULL;",
                        "CREATE TABLE \"party\" (id UUID PRIMARY KEY);",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"sent_by\" uuid NOT NULL REFERENCES \"party\"(\"id\");"
                );
    }

    @Test
    void contributeMultiple() {

        var baseChangeset = Changeset.builder()
                .baseModel(new Model(Collections.emptyList()))
                .operation("add-entity", Map.of(
                                "entity-name", "Invoice"
                        ),
                        new Model(List.of(Entity.builder()
                                .name("Invoice")
                                .build()))
                )
                .operation("add-attribute", Map.of(
                                "entity-name", "Invoice",
                                "attribute-name", "Amount",
                                "type", "LONG",
                                "naturalId", false,
                                "indexed", true,
                                "unique", false,
                                "required", false
                        ),
                        new Model(List.of(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .build()))
                ).build();

        var secondChangeset = Changeset.builder()
                .parentLoader(() -> baseChangeset)
                .baseModel(Model.builder().entities(baseChangeset.getEntities()).build())
                .operation("add-attribute", Map.of(
                                "entity-name", "Invoice",
                                "attribute-name", "Reference",
                                "type", "STRING",
                                "naturalId", true,
                                "indexed", true,
                                "unique", true,
                                "required", true
                        ), new Model(List.of(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Reference").string().unique(true).required(true).naturalId(true).indexed(true).build())
                                .build()))
                )
                .operation("add-attribute", Map.of(
                        "entity-name", "Invoice",
                        "attribute-name", "Scan",
                        "type", "CONTENT",
                        "naturalId", false,
                        "indexed", false,
                        "unique", false,
                        "required", true
                ), Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Reference").string().unique(true).required(true).naturalId(true).indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().required(true).build())
                                .build())
                        .build())
                .build();

        var thirdChangeset = Changeset.builder()
                .parentLoader(() -> secondChangeset)
                .baseModel(Model.builder().entities(secondChangeset.getEntities()).build())
                .operation("add-entity", Map.of(
                        "entity-name", "Party"
                ), Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Reference").string().unique(true).required(true).naturalId(true).indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().required(true).build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build())
                .operation("add-relation", Map.of(
                        "source-entity", "Invoice",
                        "target-entity", "Party",
                        "relation-name", "sentBy",
                        "cardinality", "MANY_TO_ONE",
                        "required", true
                ), Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Reference").string().unique(true).required(true).naturalId(true).indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().required(true).build())
                                .relation(Relation.builder().name("sentBy").source("Invoice").target("Party").manySourcePerTarget(true).required(true).build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build())
                .build();

        var description = new ScribeProjectDescription();
        description.setChangeset(thirdChangeset);

        ProjectStructure project = this.projectTester.generate(description);

        String firstChangesetPath = "src/main/resources/db/migration/V1.sql";
        String secondChangesetPath = "src/main/resources/db/migration/V2.sql";
        String thirdChangesetPath = "src/main/resources/db/migration/V3.sql";
        assertThat(project).containsFiles(
                firstChangesetPath,
                secondChangesetPath,
                thirdChangesetPath
        );
        assertThat(project).textFile(firstChangesetPath)
                .lines()
                .filteredOn(line -> !line.startsWith("--"))
                .containsExactly(
                        "CREATE TABLE \"invoice\" (id UUID PRIMARY KEY);",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"amount\" bigint NULL;",
                        "CREATE INDEX CONCURRENTLY \"invoice__amount_idx\" ON \"invoice\"(\"amount\");"
                );
        assertThat(project).textFile(secondChangesetPath)
                .lines()
                .filteredOn(line -> !line.startsWith("--"))
                .containsExactly(
                        "ALTER TABLE \"invoice\" ADD COLUMN \"reference\" text NOT NULL;",
                        "CREATE UNIQUE INDEX CONCURRENTLY \"invoice__reference_idx\" ON \"invoice\"(\"reference\");",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"scan_id\" text NOT NULL;",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"scan_length\" bigint NOT NULL;",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"scan_mimetype\" text NOT NULL;",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"scan_filename\" text NOT NULL;"
                );
        assertThat(project).textFile(thirdChangesetPath)
                .lines()
                .filteredOn(line -> !line.startsWith("--"))
                .containsExactly(
                        "CREATE TABLE \"party\" (id UUID PRIMARY KEY);",
                        "ALTER TABLE \"invoice\" ADD COLUMN \"sent_by\" uuid NOT NULL REFERENCES \"party\"(\"id\");"
                );
    }

    @SneakyThrows
    private Changeset parseChangeset(URL changesetUrl) {
        var model = objectMapper.readValue(changesetUrl, new TypeReference<EntityModel<ChangesetDto>>() {});
        var changesetFactory = new ChangesetFactory(
                (changesetDto, contentType) -> new eu.xenit.contentcloud.scribe.infrastructure.changeset.model.Model(objectMapper, changesetDto.getBaseModel()));
        return changesetFactory.create(model.getContent(), new ProjectDto("project", "org", "org/project"), MediaType.APPLICATION_JSON, null);
    }

    @ParameterizedTest
    @ArgumentsSource(ChangesetTestFixturesProvider.class)
    void contributeWithChangeset(URL changesetUrl, Resource migrationResource) {
        Changeset changeset = parseChangeset(changesetUrl);

        var description = new ScribeProjectDescription();
        description.setChangeset(changeset);

        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/resources/db/migration/V1.sql";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).hasSameContentAs(migrationResource);
    }

    static class ChangesetTestFixturesProvider implements ArgumentsProvider {
        private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            var changesets = resolver.getResources("classpath*:fixtures/db-migrations/changesets/*.json");
            return Stream.of(changesets)
                    .map(changesetResource -> {
                        try {
                            var migrationFileName = changesetResource.getFilename().replace(".json", ".sql");
                            var migrationResource = changesetResource.createRelative(migrationFileName);
                            return Arguments.of(changesetResource.getURL(), migrationResource);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }
}