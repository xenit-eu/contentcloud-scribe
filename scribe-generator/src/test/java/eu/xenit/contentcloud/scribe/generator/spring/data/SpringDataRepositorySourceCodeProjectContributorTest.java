package eu.xenit.contentcloud.scribe.generator.spring.data;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.code.java.JavaProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.version.Version;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SpringDataRepositorySourceCodeProjectContributorTest {
    private ProjectAssetTester projectTester;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
                .withConfiguration(
                        SpringDataProjectGenerationConfiguration.class,
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
    void normalEntityName() {
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder()
                                .name("document")
                                .build()
                ))
                .operations(List.of())
                .build());
        var project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/repository/DocumentRepository.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly("""
                package com.example.demo.repository;
                                
                import com.example.demo.model.Document;
                import java.util.UUID;
                import org.springframework.data.jpa.repository.JpaRepository;
                import org.springframework.data.querydsl.QuerydslPredicateExecutor;
                import org.springframework.data.rest.core.annotation.RepositoryRestResource;
                                
                @RepositoryRestResource
                interface DocumentRepository extends JpaRepository<Document, UUID>, QuerydslPredicateExecutor<Document> {
                }
                """.split("\n")
        );
    }

    @Test
    void kebabCaseEntityName() {
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder()
                                .name("claim-document")
                                .build()
                ))
                .operations(List.of())
                .build());
        var project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/repository/ClaimDocumentRepository.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly("""
                package com.example.demo.repository;
                                
                import com.example.demo.model.ClaimDocument;
                import java.util.UUID;
                import org.springframework.data.jpa.repository.JpaRepository;
                import org.springframework.data.querydsl.QuerydslPredicateExecutor;
                import org.springframework.data.rest.core.annotation.RepositoryRestResource;
                                
                @RepositoryRestResource(path = "claim-documents", collectionResourceRel = "claim-documents", itemResourceRel = "claim-document")
                interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, UUID>, QuerydslPredicateExecutor<ClaimDocument> {
                }
                """.split("\n")
        );
    }
}