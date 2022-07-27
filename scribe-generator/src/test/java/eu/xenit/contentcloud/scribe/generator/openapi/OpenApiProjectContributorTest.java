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
                                .relation(Relation.builder().name("subsidiary").required(false).source("party").target("party")
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
                - name: "party"
                paths:
                  /parties:
                    get:
                      tags:
                      - "party"
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
                                $ref: "#/components/schemas/partiesCollection"
                        "405":
                          description: "Not Allowed"
                    post:
                      tags:
                      - "party"
                      operationId: "create-parties"
                      requestBody:
                        description: "Create party"
                        required: true
                        content:
                          application/json:
                            schema:
                              $ref: "#/components/schemas/partyPostModel"
                      responses:
                        "201":
                          description: "Created"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/partyResponse"
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                  /parties/{id}:
                    get:
                      tags:
                      - "party"
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
                                $ref: "#/components/schemas/partyResponse"
                        "405":
                          description: "Not Allowed"
                    put:
                      tags:
                      - "party"
                      operationId: "update-party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      requestBody:
                        description: "Update party"
                        required: true
                        content:
                          application/json:
                            schema:
                              $ref: "#/components/schemas/partyPutModel"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/partyResponse"
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                    patch:
                      tags:
                      - "party"
                      operationId: "patch-party"
                      parameters:
                      - name: "id"
                        in: "path"
                        required: true
                        schema:
                          type: "string"
                      requestBody:
                        description: "Partially update party"
                        required: true
                        content:
                          application/json:
                            schema:
                              $ref: "#/components/schemas/partyPatchModel"
                      responses:
                        "200":
                          description: "OK"
                          content:
                            application/json:
                              schema:
                                $ref: "#/components/schemas/partyResponse"
                        "204":
                          description: "No Content"
                        "405":
                          description: "Not Allowed"
                    delete:
                      tags:
                      - "party"
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
                      - "party"
                      operationId: "get-summary"
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
                      - "party"
                      operationId: "update-summary"
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
                      - "party"
                      operationId: "delete-summary"
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
                      - "party"
                      operationId: "get-subsidiary"
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
                                $ref: "#/components/schemas/partyResponse"
                        "405":
                          description: "Not Allowed"
                    put:
                      tags:
                      - "party"
                      operationId: "update-subsidiary"
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
                              example: "/party/5"
                      responses:
                        "204":
                          description: "No Content"
                        "400":
                          description: "Bad Request"
                    post:
                      tags:
                      - "party"
                      operationId: "create-subsidiary"
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
                              example: "/party/5"
                      responses:
                        "204":
                          description: "No Content"
                        "400":
                          description: "Bad Request"
                    delete:
                      tags:
                      - "party"
                      operationId: "delete-subsidiary"
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
                    partyResponse:
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
                    partiesCollection:
                      type: "object"
                      properties:
                        _embedded:
                          type: "object"
                          properties:
                            parties:
                              type: "array"
                              items:
                                $ref: "#/components/schemas/partyResponse"
                        _links:
                          type: "object"
                          properties:
                            self:
                              $ref: "#/components/schemas/Link"
                            profile:
                              $ref: "#/components/schemas/Link"
                        page:
                          $ref: "#/components/schemas/page"
                    partyPostModel:
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
                    partyPutModel:
                      type: "object"
                      properties:
                        name:
                          type: "string"
                        subsidiary:
                          type: "string"
                      required:
                      - "name"
                    partyPatchModel:
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