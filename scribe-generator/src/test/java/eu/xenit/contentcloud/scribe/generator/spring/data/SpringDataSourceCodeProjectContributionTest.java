package eu.xenit.contentcloud.scribe.generator.spring.data;

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
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SpringDataSourceCodeProjectContributionTest {

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
    void testDefaultDataTypes() {
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Document")
                                .attribute(Attribute.builder("name").string().build())
                                .attribute(Attribute.builder("number").number().build())
                                .attribute(Attribute.builder("check").bool().build())
                                .attribute(Attribute.builder("uuid").uuid().build())
                                .attribute(Attribute.builder("datetime").timestamp().build())
                                .build()
                ))
                .operations(List.of())
                .build());
        var project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/model/Document.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly("""
                package com.example.demo.model;
                                
                import java.lang.String;
                import java.time.Instant;
                import java.util.UUID;
                import javax.persistence.Entity;
                import javax.persistence.GeneratedValue;
                import javax.persistence.GenerationType;
                import javax.persistence.Id;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                                
                @Entity
                @NoArgsConstructor
                @Getter
                @Setter
                public class Document {
                \t@Id
                \t@GeneratedValue(strategy = GenerationType.AUTO)
                \tprivate UUID id;
                                
                \tprivate String name;
                                
                \tprivate long number;
                                
                \tprivate boolean check;
                                
                \tprivate UUID uuid;
                                
                \tprivate Instant datetime;
                }
                """.split("\n")
        );
    }

    @Test
    void keywordFieldNames_shouldBePrefixed() {
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Package")
                                .attribute(Attribute.builder("code").number().naturalId(true).build())
                                .build(),
                        Entity.builder().name("Document")
                                .attribute(Attribute.builder("name").string().build())
                                .attribute(Attribute.builder("public").bool().build())
                                .relation(Relation.builder().name("package").source("Document").target("Package").manySourcePerTarget(true).build())
                                .build()
                ))
                .operations(List.of())
                .build());
        var project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/model/Document.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly("""
                package com.example.demo.model;
                                
                import com.fasterxml.jackson.annotation.JsonProperty;
                import java.lang.String;
                import java.util.UUID;
                import javax.persistence.Entity;
                import javax.persistence.GeneratedValue;
                import javax.persistence.GenerationType;
                import javax.persistence.Id;
                import javax.persistence.ManyToOne;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                import org.springframework.data.rest.core.annotation.RestResource;
                
                @Entity
                @NoArgsConstructor
                @Getter
                @Setter
                public class Document {
                \t@Id
                \t@GeneratedValue(strategy = GenerationType.AUTO)
                \tprivate UUID id;
                
                \tprivate String name;
                                
                \t@JsonProperty("public")
                \tprivate boolean _public;
                
                \t@ManyToOne
                \t@RestResource(rel = "package", path = "package")
                \t@JsonProperty("package")
                \tprivate Package _package;
                }
                """.split("\n")
        );
    }

    @Test
    void oneToOneRelation() {
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("PurchaseOrder")
                                .attribute(Attribute.builder("number").string().naturalId(true).build())
                                .attribute(Attribute.builder("description").string().build())
                                .build(),
                        Entity.builder().name("Invoice")
                                .attribute(Attribute.builder("number").string().naturalId(true).build())
                                .relation(Relation.builder().name("po").source("Invoice").target("PurchaseOrder")
                                        .required(true).build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/model/Invoice.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly("""
                package com.example.demo.model;
                                
                import java.lang.String;
                import java.util.UUID;
                import javax.persistence.Entity;
                import javax.persistence.GeneratedValue;
                import javax.persistence.GenerationType;
                import javax.persistence.Id;
                import javax.persistence.OneToOne;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                                
                @Entity
                @NoArgsConstructor
                @Getter
                @Setter
                public class Invoice {
                \t@Id
                \t@GeneratedValue(strategy = GenerationType.AUTO)
                \tprivate UUID id;
                                
                \tprivate String number;
                                
                \t@OneToOne(optional = false)
                \tprivate PurchaseOrder po;
                }
                """.split("\n")
        );
    }

    @Test
    void manyToOneRelation() {
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Party")
                                .attribute(Attribute.builder("VAT").string().naturalId(true).build())
                                .attribute(Attribute.builder("name").string().build())
                                .build(),
                        Entity.builder().name("Invoice")
                                .attribute(Attribute.builder("number").string().naturalId(true).build())
                                .relation(Relation.builder()
                                        .name("counterparty")
                                        .source("Invoice")
                                        .target("Party")
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(false)
                                        .required(true)
                                        .build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/model/Invoice.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly("""
                package com.example.demo.model;
                                
                import java.lang.String;
                import java.util.UUID;
                import javax.persistence.Entity;
                import javax.persistence.GeneratedValue;
                import javax.persistence.GenerationType;
                import javax.persistence.Id;
                import javax.persistence.ManyToOne;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                                
                @Entity
                @NoArgsConstructor
                @Getter
                @Setter
                public class Invoice {
                \t@Id
                \t@GeneratedValue(strategy = GenerationType.AUTO)
                \tprivate UUID id;
                                
                \tprivate String number;
                                
                \t@ManyToOne(optional = false)
                \tprivate Party counterparty;
                }
                """.split("\n")
        );
    }

    @Test
    void oneToMany_usingJoinColumn() {
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Chapter")
                                .attribute(Attribute.builder("name").string().build())
                                .build(),
                        Entity.builder().name("Book")
                                .attribute(Attribute.builder("isbn").string().naturalId(true).build())
                                .attribute(Attribute.builder("title").string().build())
                                .relation(Relation.builder()
                                        .name("chapters")
                                        .source("Book")
                                        .target("Chapter")
                                        .manyTargetPerSource(true)
                                        .build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/java/com/example/demo/model/Book.java";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly("""
                package com.example.demo.model;
                                
                import java.lang.String;
                import java.util.List;
                import java.util.UUID;
                import javax.persistence.Entity;
                import javax.persistence.GeneratedValue;
                import javax.persistence.GenerationType;
                import javax.persistence.Id;
                import javax.persistence.JoinColumn;
                import javax.persistence.OneToMany;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                                
                @Entity
                @NoArgsConstructor
                @Getter
                @Setter
                public class Book {
                \t@Id
                \t@GeneratedValue(strategy = GenerationType.AUTO)
                \tprivate UUID id;
                                
                \tprivate String isbn;
                                
                \tprivate String title;
                                
                \t@OneToMany
                \t@JoinColumn(name = "_book_id__chapters")
                \tprivate List<Chapter> chapters;
                }
                """.split("\n")
        );
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