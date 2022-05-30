package eu.xenit.contentcloud.scribe.generator.openapi;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OpenApiProjectContributorTest {

    private ProjectAssetTester projectTester;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
                .withConfiguration(
                        OpenApiProjectGenerationConfiguration.class,
                        SpringDataProjectGenerationConfiguration.class,
                        JavaProjectGenerationConfiguration.class)
                .withDirectory(directory)
                .withDescriptionCustomizer((description) -> {
                    description.setLanguage(new JavaLanguage());
                    description.setPlatformVersion(Version.parse("2.6.6"));
                    description.setBuildSystem(new GradleBuildSystem());
                });
    }

    @Test
    void openApiSpec_isContributed() {
        var description = new ScribeProjectDescription();
        description.setChangeset(Changeset.builder()
                .entities(List.of(
                        Entity.builder().name("Party")
                                .attribute(Attribute.builder("VAT").string().naturalId(true).build())
                                .attribute(Attribute.builder("name").string().build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "openapi.yml";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly(
                "openapi: \"3.0.2\"",
                "info:",
                "  description: \"broker-content\"",
                "  version: \"1.0.6\"",
                "  title: \"Maelstrom Money\"",
                "tags:",
                "- name: \"Party\"",
                "paths:",
                "  /Party:",
                "    get:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"page\"",
                "        in: \"query\"",
                "        required: false",
                "        schema:",
                "          type: \"integer\"",
                "      - name: \"size\"",
                "        in: \"query\"",
                "        required: false",
                "        schema:",
                "          type: \"integer\"",
                "      - name: \"sort\"",
                "        in: \"query\"",
                "        required: false",
                "        schema:",
                "          type: \"string\"",
                "      responses:",
                "        \"200\":",
                "          description: \"\"",
                "        \"405\":",
                "          description: \"List is not supported\"",
                "    head:",
                "    post:",

                "components:",
                "  schemas:",
                "    Party:",
                "      type: \"object\"",
                "      properties:",
                "        vat:",
                "          type: \"string\"",
                "        name:",
                "          type: \"string\"",
                "        _links:",
                "          type: \"object\"",
                "          properties:",
                "            self:",
                "              $ref: \"#/components/schemas/Link\"",
                "            party:",
                "              $ref: \"#/components/schemas/Link\"",
                "    Link:",
                "      type: \"object\"",
                "      properties:",
                "        href:",
                "          type: \"string\""
        );
    }

}