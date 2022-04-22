package eu.xenit.contentcloud.scribe.generator.repository;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.entitymodel.EntityModelGenerationConfiguration;
import eu.xenit.contentcloud.scribe.generator.source.SourceCodeProjectGenerationConfiguration;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.code.java.JavaProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositoriesPoetSourceCodeProjectContributorTest {

    private ProjectAssetTester projectTester;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
                .withConfiguration(
                        SourceCodeProjectGenerationConfiguration.class,
                        EntityModelGenerationConfiguration.class,
                        RepositoryProjectGenerationConfiguration.class,
                        JavaProjectGenerationConfiguration.class)
                .withDirectory(directory)
                .withDescriptionCustomizer((description) -> {
                    description.setLanguage(new JavaLanguage());
                    if (description.getPlatformVersion() == null) {
                        description.setPlatformVersion(Version.parse("2.6.6"));
                    }
                    description.setBuildSystem(new GradleBuildSystem());
                });
    }

    @Test
    void repositoryClassIsContributed() {

        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Party")
                                .attribute(Attribute.builder("VAT").string().naturalId(true).build())
                                .attribute(Attribute.builder("name").string().build())
                                .build(),
                        Entity.builder().name("Invoice")
                                .attribute(Attribute.builder("number").string().naturalId(true).build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/repository/InvoiceRepository.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly(
                "package com.example.demo.repository;",
                "",
                "import com.example.demo.model.Invoice;",
                "import java.util.UUID;",
                "import org.springframework.data.jpa.repository.JpaRepository;",
                "import org.springframework.data.querydsl.QuerydslPredicateExecutor;",
                "import org.springframework.data.rest.core.annotation.RepositoryRestResource;",
                "",
                "@RepositoryRestResource",
                "interface InvoiceRepository extends JpaRepository<Invoice, UUID>, QuerydslPredicateExecutor<Invoice> {",
                "}"
        );
    }

}