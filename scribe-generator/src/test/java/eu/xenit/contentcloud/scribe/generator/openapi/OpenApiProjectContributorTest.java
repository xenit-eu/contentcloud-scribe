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
                                .attribute(Attribute.builder("Vat").string().naturalId(true).build())
                                .attribute(Attribute.builder("name").string().build())
                                .relation(Relation.builder().name("Subsidiary").required(false).source("Party").target("Party")
                                        .manyTargetPerSource(true).manySourcePerTarget(false).build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "openapi.yml";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly(
<<<<<<< HEAD
                """
                openapi: "3.0.2"
                info:
                  description: "broker-content"
                  version: "1.0.6"
                  title: "Maelstrom Money"
                servers:
                - url: "http://localhost:8000"
                tags:
                - name: "Party"
                paths:
                  /parties:
                    get:
                      tags:
                      - "Party"
                      parameters:
                      - name: "page"
                        in: "query"
                        required: false
                        schema:
                          type: "integer"
                      - name: "size"
                        in: "query"
                        required: false
                        schema:
                          type: "integer"
                      - name: "sort"
                        in: "query"
                        required: false
                        schema:
                          type: "string"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/partiesCollection"
                        "405":
                          description: "Not Allowed"
                    head:
                      tags:
                      - "Party"
                      responses:
                        "204":
                          description: "No Content"
                        "404":
                          description: "Not Found"
                    post:
                      tags:
                      - "Party"
                      requestBody:
                        description: "Create Party"
                        required: true
                        content:
                          application/json:
                            schema:
                              $ref: "#/components/schemas/Party"
                      responses:
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                  /parties/{id}:
                    get:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                allOf:
                                - $ref: "#/components/schemas/Party"
                                - $ref: "#/components/schemas/PartyLinks"
                        "405":
                          description: "Not Allowed"
                    head:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      responses:
                        "204":
                          description: "No Content"
                        "404":
                          description: "Not Found"
                    put:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      responses:
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                    patch:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      responses:
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                    delete:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      responses:
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                  /parties/{id}/subsidiary:
                    get:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                allOf:
                                - $ref: "#/components/schemas/Party"
                                - $ref: "#/components/schemas/PartyLinks"
                        "405":
                          description: "Not Allowed"
                    put:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      requestBody:
                        description: "Create Party"
                        required: true
                        content:
                          text/uri-list:
                            schema:
                              type: "string"
                              example: "/Party/5"
                      responses:
                        "204":
                          description: "No Content"
                        "400":
                          description: "Bad Request"
                    post:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      requestBody:
                        description: "Create Party"
                        required: true
                        content:
                          text/uri-list:
                            schema:
                              type: "string"
                              example: "/Party/5"
                      responses:
                        "204":
                          description: "No Content"
                        "400":
                          description: "Bad Request"
                    delete:
                      tags:
                      - "Party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "integer"
                      responses:
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                components:
                  schemas:
                    Party:
                      type: "object"
                      properties:
                        vat:
                          type: "string"
                        name:
                          type: "string"
                    PartyLinks:
                      type: "object"
                      properties:
                        _links:
                          type: "object"
                          properties:
                            self:
                              $ref: "#/components/schemas/Link"
                            party:
                              $ref: "#/components/schemas/Link"
                    partiesCollection:
                      type: "object"
                      properties:
                        _embedded:
                          type: "object"
                          properties:
                            parties:
                              type: "array"
                              items:
                                $ref: "#/components/schemas/Party"
                        _links:
                          $ref: "#/components/schemas/PartyLinks"
                        page:
                          $ref: "#/components/schemas/page"
                    page:
                      type: "object"
                      properties:
                        size:
                          type: "integer"
                        totalElements:
                          type: "integer"
                        totalPages:
                          type: "integer"
                        number:
                          type: "integer"
                    Link:
                      type: "object"
                      properties:
                        href:
                          type: "string"
                """.split("\n")
=======
                "openapi: \"3.0.2\"",
                "info:",
                "  description: \"broker-content\"",
                "  version: \"1.0.6\"",
                "  title: \"Maelstrom Money\"",
                "tags:",
                "- name: \"Party\"",
                "paths:",
                "  /parties:",
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
                "          description: \"OK\"",
                "          content:",
                "            application/json:",
                "              schema:",
                "                $ref: \"#/components/schemas/Party\"",
                "        \"405\":",
                "          description: \"List is not supported\"",
                "    head:",
                "      tags:",
                "      - \"Party\"",
                "      responses:",
                "        \"204\":",
                "          description: \"\"",
                "        \"404\":",
                "          description: \"\"",
                "    post:",
                "      tags:",
                "      - \"Party\"",
                "      requestBody:",
                "        description: \"Create Party\"",
                "        required: true",
                "        content:",
                "          application/json:",
                "            schema:",
                "              $ref: \"#/components/schemas/Party\"",
                "      responses:",
                "        \"204\":",
                "          description: \"\"",
                "        \"405\":",
                "          description: \"\"",

                "  /parties/{id}:",
                "    get:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      responses:",
                "        \"200\":",
                "          description: \"OK\"",
                "          content:",
                "            application/json:",
                "              schema:",
                "                $ref: \"#/components/schemas/Party\"",
                "        \"405\":",
                "          description: \"\"",
                "    head:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      responses:",
                "        \"204\":",
                "          description: \"\"",
                "        \"404\":",
                "          description: \"\"",
                "    put:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      responses:",
                "        \"204\":",
                "          description: \"\"",
                "        \"405\":",
                "          description: \"\"",
                "    patch:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      responses:",
                "        \"405\":",
                "          description: \"\"",
                "    delete:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      responses:",
                "        \"204\":",
                "          description: \"\"",
                "        \"405\":",
                "          description: \"\"",

                "  /parties/{id}/subsidiary:",
                "    get:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      responses:",
                "        \"200\":",
                "          description: \"OK\"",
                "          content:",
                "            application/json:",
                "              schema:",
                "                $ref: \"#/components/schemas/Party\"",
                "        \"405\":",
                "          description: \"\"",
                "    put:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      requestBody:",
                "        description: \"Create Party\"",
                "        required: true",
                "        content:",
                "          text/uri-list:",
                "            schema:",
                "              type: \"string\"",
                "              example: \"/Party/5\"",
                "      responses:",
                "        \"204\":",
                "          description: \"\"",
                "        \"400\":",
                "          description: \"\"",
                "    post:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      requestBody:",
                "        description: \"Create Party\"",
                "        required: true",
                "        content:",
                "          text/uri-list:",
                "            schema:",
                "              type: \"string\"",
                "              example: \"/Party/5\"",
                "      responses:",
                "        \"204\":",
                "          description: \"\"",
                "        \"400\":",
                "          description: \"\"",
                "    delete:",
                "      tags:",
                "      - \"Party\"",
                "      parameters:",
                "      - name: \"id\"",
                "        in: \"path\"",
                "        required: true",
                "        schema:",
                "          type: \"integer\"",
                "      responses:",
                "        \"204\":",
                "          description: \"\"",
                "        \"405\":",
                "          description: \"\"",

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
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        );
    }

}