package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.openapi.model.*;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiParameters.ParameterType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.atteo.evo.inflector.English;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class OpenApiProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;
    private final EntityModel entityModel;

    private final OpenApiWriter openApiWriter = new OpenApiWriter();

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path openapiPath = projectRoot.resolve("openapi.yml");

        var model = this.createOpenApiModel();

        try (var writer = Files.newBufferedWriter(openapiPath)) {
            openApiWriter.writeOpenApiSpec(writer, model);
        }
    }

    OpenApiModel createOpenApiModel() {
        OpenApiInfo info = new OpenApiInfo("broker-content", "1.0.6", "Maelstrom Money");
        var model = new OpenApiModel("3.0.2", info, new OpenApiComponents());

        model.getServers().add(new OpenApiServers("http://localhost:8000"));

        for (Entity entity : entityModel.entities()) {
            this.contributeEntityToOpenApiModel(entity, model);
        }

        return model;
    }

    private void contributeEntityToOpenApiModel(Entity entity, OpenApiModel model) {

        // 1. add the entity as a tag
        OpenApiTags entityTag = new OpenApiTags(entity.getName());
        model.getTags().add(entityTag);

        // 2. add all the paths for the entity
        // 2a. collections resource - GET or POST /accountStateZips
        this.contributeCollectionResources(entity, model);

        // 2b. item resource - GET /accountStateZips/143
        this.contributeItemResources(entity, model);

        // 2c. association resource - GET /accountStateZips/143/insurer
         this.contributeAssociationResource(entity, model);
    }

    // Add all the collection paths
    // GET|HEAD|POST /{repository}/
    // For example: GET /accountStateZips?page=1&size=10
    private void contributeCollectionResources(Entity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        // GET
        var pathGet = new OpenApiModelPath(tags);

        pathGet.getParameters().addAll(List.of(
                new OpenApiParameters("page", ParameterType.QUERY, false, OpenApiDataTypes.INTEGER),
                new OpenApiParameters("size", ParameterType.QUERY, false, OpenApiDataTypes.INTEGER),
                new OpenApiParameters("sort", ParameterType.QUERY, false, OpenApiDataTypes.STRING)
        ));
        pathGet.getResponses().putAll(Map.of(
                "200", new OpenApiResponse("OK", Map.of("application/json",
<<<<<<< HEAD
                        new OpenApiMediaTypeObject(createOrReferenceCollectionModel(model, entity)))),
                "405", new OpenApiResponse("Not Allowed")
=======
                        new OpenApiMediaTypeObject(createOrReferenceModel(model, entity)))),
                "405", new OpenApiResponse("List is not supported")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        ));
        pathMap.put("get", pathGet);

        // HEAD
        var pathHead = new OpenApiModelPath(tags);
        pathHead.getResponses().putAll(Map.of(
<<<<<<< HEAD
                "204", new OpenApiResponse("No Content"),
                "404", new OpenApiResponse("Not Found")
=======
                "204", new OpenApiResponse(""),
                "404", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        ));
        pathMap.put("head", pathHead);

        // POST
        var pathPost = new OpenApiModelPath(tags);
        pathPost.getResponses().putAll(Map.of(
<<<<<<< HEAD
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
=======
                "204", new OpenApiResponse(""),
                "405", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        ));
        pathPost.setRequestBody(new OpenApiRequestBody(
                "Create " + entity.getName(), true,
                Map.of("application/json", new OpenApiMediaTypeObject(createOrReferenceModel(model, entity))))
        );
        pathMap.put("post", pathPost);

        model.getPaths().put("/" + modifiedName(entity.getName()), pathMap);
    }

    private void contributeItemResources(Entity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        // GET
        var pathGet = new OpenApiModelPath(tags);
        pathGet.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
<<<<<<< HEAD
        var allOf = new OpenApiAllOfReference();
        allOf.getAllOf().add(createOrReferenceModel(model, entity));
        allOf.getAllOf().add(new OpenApiReferenceObject("#/components/schemas/" + entity.getName() + "Links"));
        pathGet.getResponses().putAll(Map.of(
                "200", new OpenApiResponse("OK", Map.of("application/json", new OpenApiMediaTypeObject(allOf))),
                "405", new OpenApiResponse("Not Allowed")
=======
        pathGet.getResponses().putAll(Map.of(
                "200", new OpenApiResponse("OK", Map.of("application/json",
                        new OpenApiMediaTypeObject(createOrReferenceModel(model, entity)))),
                "405", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        ));
        pathMap.put("get", pathGet);

        // HEAD
        var pathHead = new OpenApiModelPath(tags);
        pathHead.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
        pathHead.getResponses().putAll(Map.of(
<<<<<<< HEAD
                "204", new OpenApiResponse("No Content"),
                "404", new OpenApiResponse("Not Found")
=======
                "204", new OpenApiResponse(""),
                "404", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        ));
        pathMap.put("head", pathHead);

        // PUT
        var pathPut = new OpenApiModelPath(tags);
        pathPut.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
        pathPut.getResponses().putAll(Map.of(
<<<<<<< HEAD
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
=======
                "204", new OpenApiResponse(""),
                "405", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        ));
        pathMap.put("put", pathPut);

        // PATCH
        var pathPatch = new OpenApiModelPath(tags);
        pathPatch.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
        pathPatch.getResponses().putAll(Map.of(
<<<<<<< HEAD
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
=======
                "405", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        ));
        pathMap.put("patch", pathPatch);

        // DELETE
        var pathDelete = new OpenApiModelPath(tags);
        pathDelete.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
        pathDelete.getResponses().putAll(Map.of(
<<<<<<< HEAD
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
=======
                "204", new OpenApiResponse(""),
                "405", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        ));
        pathMap.put("delete", pathDelete);

        model.getPaths().put("/" + modifiedName(entity.getName()) + "/{id}", pathMap);
    }

    // GET PUT POST DELETE
    private void contributeAssociationResource(Entity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        var relations = entity.getRelations();
        for (var relation : relations) {
            // GET
            var pathGet = new OpenApiModelPath(tags);
            pathGet.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
            ));
<<<<<<< HEAD
            var allOf = new OpenApiAllOfReference();
            allOf.getAllOf().add(createOrReferenceModel(model, entity));
            allOf.getAllOf().add(new OpenApiReferenceObject("#/components/schemas/" + entity.getName() + "Links"));
            pathGet.getResponses().putAll(Map.of(
                    "200", new OpenApiResponse("OK", Map.of("application/json",
                            new OpenApiMediaTypeObject(allOf))),
                    "405", new OpenApiResponse("Not Allowed")
=======
            pathGet.getResponses().putAll(Map.of(
                    "200", new OpenApiResponse("OK", Map.of("application/json",
                            new OpenApiMediaTypeObject(createOrReferenceModel(model, entity)))),
                    "405", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
            ));
            pathMap.put("get", pathGet);

            // PUT
            var pathPut = new OpenApiModelPath(tags);
            pathPut.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
            ));
            pathPut.getResponses().putAll(Map.of(
<<<<<<< HEAD
                    "204", new OpenApiResponse("No Content"),
                    "400", new OpenApiResponse("Bad Request")
=======
                    "204", new OpenApiResponse(""),
                    "400", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
            ));
            pathPut.setRequestBody(new OpenApiRequestBody(
                    "Create " + entity.getName(), true,
                    Map.of("text/uri-list", new OpenApiMediaTypeObject(new OpenApiRelationReference("/" + entity.getName() + "/5"))))
            );
            pathMap.put("put", pathPut);

            var pathPost = new OpenApiModelPath(tags);
            pathPost.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
            ));
            pathPost.getResponses().putAll(Map.of(
<<<<<<< HEAD
                    "204", new OpenApiResponse("No Content"),
                    "400", new OpenApiResponse("Bad Request")
=======
                    "204", new OpenApiResponse(""),
                    "400", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
                    ));
            pathPost.setRequestBody(new OpenApiRequestBody(
                    "Create " + entity.getName(), true,
                    Map.of("text/uri-list", new OpenApiMediaTypeObject(new OpenApiRelationReference("/" + entity.getName() + "/5"))))
            );
            pathMap.put("post", pathPost);

            // DELETE
            var pathDelete = new OpenApiModelPath(tags);
            pathDelete.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
            ));
            pathDelete.getResponses().putAll(Map.of(
<<<<<<< HEAD
                    "204", new OpenApiResponse("No Content"),
                    "405", new OpenApiResponse("Not Allowed")
=======
                    "204", new OpenApiResponse(""),
                    "405", new OpenApiResponse("")
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
            ));
            pathMap.put("delete", pathDelete);

            model.getPaths().put("/" + modifiedName(entity.getName()) + "/{id}/" +
                    StringUtils.uncapitalize(relation.getName()), pathMap);
        }
    }

    private OpenApiReferenceObject createOrReferenceModel(OpenApiModel model, Entity entity) {
        // figure out how to represent the entity attributes in swagger
        // if it already exists, just get the name
        // otherwise, add it first to model.components, before returning the reference

        if (!model.getComponents().getSchemas().containsKey(entity.getName())) {
            var attributes = entity.getAttributes();
            var entitySchema = new OpenApiObjectDataType();
            for (var attribute : attributes) {
                entitySchema.getProperties().putAll(Map.of(
                        StringUtils.uncapitalize(attribute.getName()), OpenApiDataTypes.STRING));
            }
<<<<<<< HEAD
            var entityLinkSchema = new OpenApiObjectDataType();
            entityLinkSchema.getProperties().putAll(Map.of(
=======
            entitySchema.getProperties().putAll(Map.of(
                    "name", OpenApiDataTypes.STRING,
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
                    "_links", new OpenApiObjectDataType(Map.of(
                            "self", new OpenApiReferenceObject("#/components/schemas/Link"),
                            StringUtils.uncapitalize(entity.getName()), new OpenApiReferenceObject("#/components/schemas/Link"))
            )));
            model.getComponents().getSchemas().put(entity.getName(), entitySchema);
<<<<<<< HEAD
            model.getComponents().getSchemas().put(entity.getName() + "Links", entityLinkSchema);
=======
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
        }

        if (!model.getComponents().getSchemas().containsKey("Link")) {
            var linkSchema = new OpenApiObjectDataType();
            linkSchema.getProperties().putAll(Map.of("href", OpenApiDataTypes.STRING));
            model.getComponents().getSchemas().put("Link", linkSchema);
        }

        // return a reference to the entity-model we just created
        return new OpenApiReferenceObject("#/components/schemas/" + entity.getName());
    }

<<<<<<< HEAD
    private OpenApiReferenceObject createOrReferenceCollectionModel(OpenApiModel model, Entity entity) {
        // add collection item to model.components, before returning the reference
        if (!model.getComponents().getSchemas().containsKey("Page")) {
            var pageSchema = new OpenApiObjectDataType();
            pageSchema.getProperties().putAll(Map.of(
                    "size", OpenApiDataTypes.INTEGER,
                    "totalElements", OpenApiDataTypes.INTEGER,
                    "totalPages", OpenApiDataTypes.INTEGER,
                    "number", OpenApiDataTypes.INTEGER
            ));
            model.getComponents().getSchemas().put("page", pageSchema);
        }

        if (!model.getComponents().getSchemas().containsKey(modifiedName(entity.getName()) + "Collection")) {
            var collectionSchema = new OpenApiObjectDataType();
            collectionSchema.getProperties().putAll(Map.of(
                    "_embedded", new OpenApiObjectDataType(Map.of(
                            modifiedName(entity.getName()), new OpenApiArrayDataType(
                                    new OpenApiReferenceObject("#/components/schemas/" + entity.getName())))),
                    "_links", new OpenApiReferenceObject("#/components/schemas/" + entity.getName() + "Links"),
                    "page", new OpenApiReferenceObject("#/components/schemas/page")
            ));
            model.getComponents().getSchemas().put(modifiedName(entity.getName()) + "Collection", collectionSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + modifiedName(entity.getName()) + "Collection");

    }

=======
>>>>>>> ad7b07a1d6c6895cb9c9b11b5619274ac9f2c157
    private static String modifiedName(String myString) {
        return English.plural(StringUtils.uncapitalize(myString));
    }
}