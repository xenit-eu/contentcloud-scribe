package eu.xenit.contentcloud.scribe.generator.spring.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Relation;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.data.SpringDataProjectGenerationConfiguration;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.code.java.JavaProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;

class SpringContentStoreSourceCodeProjectContributorTest {

    private ProjectAssetTester projectTester;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester()
                .withIndentingWriterFactory()
                .withConfiguration(
                        JavaProjectGenerationConfiguration.class,
                        SpringDataProjectGenerationConfiguration.class,
                        SpringContentProjectionGenerationConfiguration.class)
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
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Invoice")
                                .attribute(Attribute.builder("number").string().naturalId(true).build())
                                .attribute(Attribute.builder("content").content().build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/model/Invoice.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly(
                """
                package com.example.demo.model;
                
                import java.lang.String;
                import java.util.UUID;
                import javax.persistence.Entity;
                import javax.persistence.GeneratedValue;
                import javax.persistence.GenerationType;
                import javax.persistence.Id;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                import org.springframework.content.commons.annotations.ContentId;
                import org.springframework.content.commons.annotations.ContentLength;
                import org.springframework.content.commons.annotations.MimeType;
                import org.springframework.content.commons.annotations.OriginalFileName;
                
                @Entity
                @NoArgsConstructor
                @Getter
                @Setter
                public class Invoice {
                \t@Id
                \t@GeneratedValue(strategy = GenerationType.AUTO)
                \tprivate UUID id;
                
                \tprivate String number;
                
                \t@ContentId
                \tprivate String contentId;
                
                \t@ContentLength
                \tprivate long contentLength;
                
                \t@MimeType
                \tprivate String contentMimetype;
                
                \t@OriginalFileName
                \tprivate String contentFilename;
                }
                """.split("\n")
        );
    }

    @Test
    void contentStoreIsContributed() {

        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Invoice")
                                .attribute(Attribute.builder("number").string().naturalId(true).build())
                                .attribute(Attribute.builder("content").content().build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/store/InvoiceContentStore.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly(
                "package com.example.demo.store;",
                "",
                "import com.example.demo.model.Invoice;",
                "import java.lang.String;",
                "import org.springframework.content.commons.repository.ContentStore;",
                "import org.springframework.content.rest.StoreRestResource;",
                "",
                "@StoreRestResource",
                "interface InvoiceContentStore extends ContentStore<Invoice, String> {",
                "}");
    }
}