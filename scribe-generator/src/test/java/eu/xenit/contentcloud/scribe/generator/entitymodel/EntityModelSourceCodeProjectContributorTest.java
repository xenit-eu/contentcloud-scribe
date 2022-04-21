package eu.xenit.contentcloud.scribe.generator.entitymodel;

import static io.spring.initializr.metadata.Dependency.SCOPE_ANNOTATION_PROCESSOR;
import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Relation;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.code.java.JavaProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EntityModelSourceCodeProjectContributorTest {

    private ProjectAssetTester projectTester;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
                .withConfiguration(
                        EntityModelGenerationConfiguration.class,
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
    void entityClassIsContributed() {
        var description = createProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Party")
                                .attribute(Attribute.withName("VAT").naturalId(true).typeString())
                                .attribute(Attribute.withName("name").typeString())
                                .build(),
                        Entity.builder().name("Invoice")
                                .attribute(Attribute.withName("number").naturalId(true).typeString())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/model/Invoice.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly(
                "package com.example.demo.model;",
                "",
                "import java.lang.String;",
                "import java.util.UUID;",
                "import javax.persistence.Entity;",
                "import javax.persistence.GeneratedValue;",
                "import javax.persistence.GenerationType;",
                "import javax.persistence.Id;",
                "import lombok.Getter;",
                "import lombok.NoArgsConstructor;",
                "import lombok.Setter;",
                "",
                "@Entity",
                "@NoArgsConstructor",
                "@Getter",
                "@Setter",
                "public class Invoice {",
                "\t@Id",
                "\t@GeneratedValue(strategy = GenerationType.AUTO)",
                "\tprivate UUID id;",
                "",
                "\tprivate String number;",
                "}"
        );
    }

    private static ScribeProjectDescription createProjectDescription() {
        var description = new ScribeProjectDescription();

        var lombok = Dependency.withId("lombok", "org.projectlombok", "lombok", null, SCOPE_ANNOTATION_PROCESSOR);
        description.addDependency(lombok.getId(), MetadataBuildItemMapper.toDependency(lombok));
        description.useLombok(true);

        return description;
    }
}