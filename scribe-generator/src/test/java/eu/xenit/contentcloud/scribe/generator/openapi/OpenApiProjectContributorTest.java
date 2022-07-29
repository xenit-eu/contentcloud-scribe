package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Relation;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.content.SpringContentProjectionGenerationConfiguration;
import eu.xenit.contentcloud.scribe.generator.spring.data.SpringDataProjectGenerationConfiguration;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.code.java.JavaProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiProjectContributorTest {

    private ProjectAssetTester projectTester;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
                .withConfiguration(
                        OpenApiProjectGenerationConfiguration.class,
                        SpringDataProjectGenerationConfiguration.class,
                        SpringContentProjectionGenerationConfiguration.class,
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
                                .attribute(Attribute.builder("vat").string().naturalId(true).required(true).unique(true).build())
                                .attribute(Attribute.builder("name").string().indexed(true).required(true).build())
                                .attribute(Attribute.builder("summary").content().build())
                                .relation(Relation.builder().name("subsidiary").required(false).source("Party").target("Party")
                                        .manyTargetPerSource(false).manySourcePerTarget(true).build())
                                .build()
                ))
                .operations(List.of())
                .build());
        ProjectStructure project = this.projectTester.generate(description);

        String path = "src/main/resources/META-INF/resources/openapi.yml";
        assertThat(project).containsFiles(path);
        assertThat(project).textFile(path).containsExactly(
                """
                openapi: "3.0.2"
                info:
                  version: "0.0.1-SNAPSHOT"
                  title: "DemoApplication"
                servers:
                - url: "http://localhost:8000"
                tags:
                - name: "Party"
                paths:
                  /parties:
                    get:
                      tags:
                      - "Party"
                      operationId: "get-parties"
                      parameters:
                      - name: "name"
                        in: "query"
                        required: false
                        schema:
                          type: "string"
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
                                $ref: "#/components/schemas/PartyCollection"
                        "405":
                          description: "Not Allowed"
                    post:
                      tags:
                      - "Party"
                      operationId: "create-party"
                      requestBody:
                        description: "Create Party"
                        required: true
                        content:
                          application/json:
                            schema:
                              $ref: "#/components/schemas/PartyPostModel"
                      responses:
                        "201":
                          description: "Created"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/PartyResponse"
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                  /parties/{id}:
                    get:
                      tags:
                      - "Party"
                      operationId: "get-party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/PartyResponse"
                        "405":
                          description: "Not Allowed"
                    put:
                      tags:
                      - "Party"
                      operationId: "update-party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      requestBody:
                        description: "Update Party"
                        required: true
                        content:
                          application/json:
                            schema:
                              $ref: "#/components/schemas/PartyPutModel"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/PartyResponse"
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                    patch:
                      tags:
                      - "Party"
                      operationId: "patch-party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      requestBody:
                        description: "Partially update Party"
                        required: true
                        content:
                          application/json:
                            schema:
                              $ref: "#/components/schemas/PartyPatchModel"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/PartyResponse"
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                    delete:
                      tags:
                      - "Party"
                      operationId: "delete-party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      responses:
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                  /parties/{id}/summary:
                    get:
                      tags:
                      - "Party"
                      operationId: "get-party-summary"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      responses:
                        "200":
                          description: "A file"
                          content:
                            '*/*':
                              schema:
                                type: "string"
                                format: "binary"
                        "404":
                          description: "Not Found"
                    put:
                      tags:
                      - "Party"
                      operationId: "update-party-summary"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      requestBody:
                        content:
                          '*/*':
                            schema:
                              type: "string"
                              format: "binary"
                      responses:
                        "200":
                          description: "File uploaded"
                    delete:
                      tags:
                      - "Party"
                      operationId: "delete-party-summary"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      responses:
                        "204":
                          description: "File deleted"
                  /parties/{id}/subsidiary:
                    get:
                      tags:
                      - "Party"
                      operationId: "get-party-subsidiary"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/PartyResponse"
                        "405":
                          description: "Not Allowed"
                    put:
                      tags:
                      - "Party"
                      operationId: "update-party-subsidiary"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      requestBody:
                        description: "Update subsidiary"
                        required: true
                        content:
                          text/uri-list:
                            schema:
                              type: "string"
                              example: "/parties/00000000-0000-0000-0000-000000000000"
                      responses:
                        "204":
                          description: "No Content"
                        "400":
                          description: "Bad Request"
                    post:
                      tags:
                      - "Party"
                      operationId: "create-party-subsidiary"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      requestBody:
                        description: "Create subsidiary"
                        required: true
                        content:
                          text/uri-list:
                            schema:
                              type: "string"
                              example: "/parties/00000000-0000-0000-0000-000000000000"
                      responses:
                        "204":
                          description: "No Content"
                        "400":
                          description: "Bad Request"
                    delete:
                      tags:
                      - "Party"
                      operationId: "delete-party-subsidiary"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      responses:
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                components:
                  schemas:
                    Link:
                      type: "object"
                      properties:
                        href:
                          type: "string"
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
                    PartyResponse:
                      type: "object"
                      properties:
                        vat:
                          type: "string"
                        name:
                          type: "string"
                        summaryId:
                          type: "string"
                        summaryLength:
                          type: "integer"
                        summaryMimetype:
                          type: "string"
                        summaryFilename:
                          type: "string"
                        _links:
                          type: "object"
                          properties:
                            self:
                              $ref: "#/components/schemas/Link"
                            party:
                              $ref: "#/components/schemas/Link"
                            summary:
                              $ref: "#/components/schemas/Link"
                            subsidiary:
                              $ref: "#/components/schemas/Link"
                    PartyCollection:
                      type: "object"
                      properties:
                        _embedded:
                          type: "object"
                          properties:
                            parties:
                              type: "array"
                              items:
                                $ref: "#/components/schemas/PartyResponse"
                        _links:
                          type: "object"
                          properties:
                            self:
                              $ref: "#/components/schemas/Link"
                            profile:
                              $ref: "#/components/schemas/Link"
                        page:
                          $ref: "#/components/schemas/page"
                    PartyPostModel:
                      type: "object"
                      properties:
                        vat:
                          type: "string"
                        name:
                          type: "string"
                        subsidiary:
                          type: "string"
                      required:
                      - "vat"
                      - "name"
                    PartyPutModel:
                      type: "object"
                      properties:
                        name:
                          type: "string"
                        subsidiary:
                          type: "string"
                      required:
                      - "name"
                    PartyPatchModel:
                      type: "object"
                      properties:
                        name:
                          type: "string"
                        subsidiary:
                          type: "string"
                """.split("\n")
        );
    }
}