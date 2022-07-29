package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.openapi.model.*;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiParameters.ParameterType;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntityModel;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenApiProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;
    private final RestResourceEntityModel entityModel;

    private final OpenApiYmlWriter openApiWriter = new OpenApiYmlWriter();
    private final static Map<String, String> EXAMPLE_ID = Collections.singletonMap("id", new UUID(0, 0).toString());

    @Override
    public void contribute(Path projectRoot) throws IOException {
        SourceStructure mainSource = description.getBuildSystem().getMainSource(projectRoot, description.getLanguage());
        Path mainResourcesDir = projectRoot.resolve(mainSource.getResourcesDirectory());
        Path staticResourcesDir = Files.createDirectories(mainResourcesDir.resolve("META-INF/resources"));
        Path openapiPath = staticResourcesDir.resolve("openapi.yml");

        BufferedWriter output = Files.newBufferedWriter(openapiPath);
        writeOpenApiSpec(output);
    }

    public void writeOpenApiSpec(Writer output) throws IOException {
        try (var writer = output) {
            var model = this.createOpenApiModel();
            openApiWriter.writeOpenApiSpec(writer, model);
        }
    }

    OpenApiModel createOpenApiModel() {
        OpenApiInfo info = new OpenApiInfo(description.getDescription(), description.getVersion(), description.getApplicationName());
        var model = new OpenApiModel("3.0.2", info, new OpenApiComponents());

        componentInitialization(model);

        for (RestResourceEntity entity : entityModel.entities()) {
            this.contributeEntityToOpenApiModel(entity, model);
        }

        return model;
    }

    private void contributeEntityToOpenApiModel(RestResourceEntity entity, OpenApiModel model) {

        // 1. add the entity as a tag
        OpenApiTags entityTag = new OpenApiTags(entity.getModelEntityName());
        model.getTags().add(entityTag);

        // 2. add all the paths for the entity
        // 2a. collections resource - GET or POST /accountStateZips
        this.contributeCollectionResources(entity, model);

        // 2b. item resource - GET /accountStateZips/143
        this.contributeItemResources(entity, model);

        // 2c. content attribute - GET /broker/143/summary
        this.contributeContentAttribute(entity, model);

        // 2d. association resource - GET /accountStateZips/143/insurer
        this.contributeAssociationResource(entity, model);
    }

    // Add all the collection paths
    // GET|HEAD|POST /{repository}/
    // For example: GET /accountStateZips?page=1&size=10
    private void contributeCollectionResources(RestResourceEntity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getModelEntityName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        var pathGet = new OpenApiModelPath(tags, "get-" + entity.getCollectionResource().getRelationName());

        var attributes = entity.getAttributes();
        for (var attribute : attributes) {
            if (attribute.isSearchable()) {
                pathGet.getParameters().addAll(List.of(
                        new OpenApiParameters(attribute.getRestAttributeName(), ParameterType.QUERY, false, OpenApiDataTypes.of(attribute.getType()))
                ));
            }
        }

        pathGet.getParameters().addAll(List.of(
                new OpenApiParameters("page", ParameterType.QUERY, false, OpenApiDataTypes.INTEGER),
                new OpenApiParameters("size", ParameterType.QUERY, false, OpenApiDataTypes.INTEGER),
                new OpenApiParameters("sort", ParameterType.QUERY, false, OpenApiDataTypes.STRING)
        ));
        pathGet.getResponses().putAll(linkedMapOf(
                "200", new OpenApiResponse("OK", linkedMapOf("application/json",
                        new OpenApiMediaTypeObject(createOrReferenceCollectionModel(model, entity)))),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("get", pathGet);

        var pathPost = new OpenApiModelPath(tags, "create-" + entity.getItemResource().getRelationName());
        pathPost.setRequestBody(new OpenApiRequestBody(
                "Create " + entity.getModelEntityName(), true,
                linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferencePostModel(model, entity))))
        );
        pathPost.getResponses().putAll(linkedMapOf(
                "201", new OpenApiResponse("Created", linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("post", pathPost);

        model.getPaths().put("/" + entity.getCollectionResource().getUriTemplate().toUriTemplate(), pathMap);
    }

    private void contributeItemResources(RestResourceEntity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getModelEntityName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        var pathGet = new OpenApiModelPath(tags, "get-" + entity.getItemResource().getRelationName());
        pathGet.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
        ));
        pathGet.getResponses().putAll(linkedMapOf(
                "200", new OpenApiResponse("OK", linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("get", pathGet);

        var pathPut = new OpenApiModelPath(tags, "update-" + entity.getItemResource().getRelationName());
        pathPut.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
        ));
        pathPut.setRequestBody(new OpenApiRequestBody(
                "Update " + entity.getModelEntityName(), true,
                linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferencePutModel(model, entity))))
        );
        pathPut.getResponses().putAll(linkedMapOf(
                "200", new OpenApiResponse("OK", linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("put", pathPut);

        var pathPatch = new OpenApiModelPath(tags, "patch-" + entity.getItemResource().getRelationName());
        pathPatch.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
        ));
        pathPatch.setRequestBody(new OpenApiRequestBody(
                "Partially update " + entity.getModelEntityName(), true,
                linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferencePatchModel(model, entity))))
        );
        pathPatch.getResponses().putAll(linkedMapOf(
                "200", new OpenApiResponse("OK", linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("patch", pathPatch);

        var pathDelete = new OpenApiModelPath(tags, "delete-" + entity.getItemResource().getRelationName());
        pathDelete.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
        ));
        pathDelete.getResponses().putAll(linkedMapOf(
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("delete", pathDelete);

        model.getPaths().put("/" + entity.getItemResource().getUriTemplate().toUriTemplate(), pathMap);
    }

    private void contributeContentAttribute(RestResourceEntity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getModelEntityName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();
        var attributes = entity.getAttributes();

        for (var attribute : attributes) {
            if (attribute.getType().equals("CONTENT")) {
                var pathGet = new OpenApiModelPath(tags, "get-" + entity.getItemResource().getRelationName()+"-"+attribute.getRestAttributeName());
                pathGet.getParameters().addAll(List.of(
                        new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
                ));
                pathGet.getResponses().putAll(linkedMapOf(
                        "200", new OpenApiResponse("A file", linkedMapOf("*/*", new OpenApiMediaTypeObject(OpenApiDataTypes.CONTENT))),
                        "404", new OpenApiResponse("Not Found")
                ));
                pathMap.put("get", pathGet);

                var pathPut = new OpenApiModelPath(tags, "update-" + entity.getItemResource().getRelationName()+"-"+attribute.getRestAttributeName());
                pathPut.getParameters().addAll(List.of(
                        new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
                ));
                pathPut.getResponses().putAll(linkedMapOf(
                        "200", new OpenApiResponse("File uploaded")
                ));
                pathPut.setRequestBody(new OpenApiRequestBody(null, null,
                        linkedMapOf("*/*", new OpenApiMediaTypeObject(OpenApiDataTypes.CONTENT)))
                );
                pathMap.put("put", pathPut);

                var pathDelete = new OpenApiModelPath(tags, "delete-" + entity.getItemResource().getRelationName()+"-"+attribute.getRestAttributeName());
                pathDelete.getParameters().addAll(List.of(
                        new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
                ));
                pathDelete.getResponses().putAll(linkedMapOf(
                        "204", new OpenApiResponse("File deleted")
                ));
                pathMap.put("delete", pathDelete);

                model.getPaths().put("/" + entity.getItemResource().getUriTemplate().toUriTemplate()+"/" +
                        attribute.getRestAttributeName(), pathMap);
            }
        }
    }

    private void contributeAssociationResource(RestResourceEntity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getModelEntityName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        var relations = entity.getRelations();
        for (var relation : relations) {
            var pathGet = new OpenApiModelPath(tags, "get-" + entity.getItemResource().getRelationName()+"-"+relation.getRestRelationName());
            pathGet.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
            ));
            pathGet.getResponses().putAll(linkedMapOf(
                    "200", new OpenApiResponse("OK", linkedMapOf("application/json",
                            new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                    "405", new OpenApiResponse("Not Allowed")
            ));
            pathMap.put("get", pathGet);

            var pathPut = new OpenApiModelPath(tags, "update-" +entity.getItemResource().getRelationName()+"-"+ relation.getRestRelationName());
            pathPut.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
            ));
            pathPut.getResponses().putAll(linkedMapOf(
                    "204", new OpenApiResponse("No Content"),
                    "400", new OpenApiResponse("Bad Request")
            ));
            pathPut.setRequestBody(new OpenApiRequestBody(
                    "Update " + relation.getRestRelationName(), true,
                    linkedMapOf("text/uri-list", new OpenApiMediaTypeObject(new OpenApiRelationReference("/" + entity.getItemResource().getUriTemplate().expand(EXAMPLE_ID).toUriTemplate()))))
            );
            pathMap.put("put", pathPut);

            var pathPost = new OpenApiModelPath(tags, "create-" +entity.getItemResource().getRelationName()+"-"+ relation.getRestRelationName());
            pathPost.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
            ));
            pathPost.getResponses().putAll(linkedMapOf(
                    "204", new OpenApiResponse("No Content"),
                    "400", new OpenApiResponse("Bad Request")
                    ));
            pathPost.setRequestBody(new OpenApiRequestBody(
                    "Create " + relation.getRestRelationName(), true,
                linkedMapOf("text/uri-list", new OpenApiMediaTypeObject(new OpenApiRelationReference("/" + entity.getItemResource().getUriTemplate().expand(EXAMPLE_ID).toUriTemplate()))))
            );
            pathMap.put("post", pathPost);

            var pathDelete = new OpenApiModelPath(tags, "delete-" +entity.getItemResource().getRelationName()+"-"+ relation.getRestRelationName());
            pathDelete.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
            ));
            pathDelete.getResponses().putAll(linkedMapOf(
                    "204", new OpenApiResponse("No Content"),
                    "405", new OpenApiResponse("Not Allowed")
            ));
            pathMap.put("delete", pathDelete);

            model.getPaths().put("/" + relation.getUriTemplate().toUriTemplate(), pathMap);
        }
    }

    private OpenApiReferenceObject createOrReferencePostModel(OpenApiModel model, RestResourceEntity entity) {
        if (!model.getComponents().getSchemas().containsKey(entity.getModelEntityName() + "PostModel")) {
            var attributes = entity.getAttributes();
            var requestSchema = new OpenApiObjectDataType();
            LinkedHashMap<String, OpenApiSchema> attributesMap = linkedMapOf(null, null);
            for (var attribute : attributes) {
                if (!(attribute.getType().equals("CONTENT"))) {
                    attributesMap.put(attribute.getModelAttributeName(), OpenApiDataTypes.of(attribute.getType()));
                }
                if (attribute.isRequired()) {
                    requestSchema.getRequired().add(attribute.getModelAttributeName());
                }
            }
            var relations = entity.getRelations();
            for (var relation: relations) {
                if (!(relation.isManyTargetPerSource())) {
                    attributesMap.put(relation.getRestRelationName(), OpenApiDataTypes.STRING);
                }
            }
            requestSchema.getProperties().putAll(attributesMap);
            model.getComponents().getSchemas().put(entity.getModelEntityName() + "PostModel", requestSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + entity.getModelEntityName() + "PostModel");
    }

    private OpenApiReferenceObject createOrReferencePutModel(OpenApiModel model, RestResourceEntity entity) {
        if (!model.getComponents().getSchemas().containsKey(entity.getModelEntityName() + "PutModel")) {
            var attributes = entity.getAttributes();
            var requestSchema = new OpenApiObjectDataType();
            LinkedHashMap<String, OpenApiSchema> attributesMap = linkedMapOf(null, null);
            for (var attribute : attributes) {
                if (!(attribute.getType().equals("CONTENT")) && !attribute.isNaturalId()) {
                    attributesMap.put(attribute.getModelAttributeName(), OpenApiDataTypes.of(attribute.getType()));
                }
                if (attribute.isRequired() && !attribute.isNaturalId()) {
                    requestSchema.getRequired().add(attribute.getModelAttributeName());
                }
            }
            var relations = entity.getRelations();
            for (var relation: relations) {
                if (!(relation.isManyTargetPerSource())) {
                    attributesMap.put(relation.getRestRelationName(), OpenApiDataTypes.STRING);
                }
            }
            requestSchema.getProperties().putAll(attributesMap);
            model.getComponents().getSchemas().put(entity.getModelEntityName() + "PutModel", requestSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + entity.getModelEntityName() + "PutModel");
    }

    private OpenApiReferenceObject createOrReferencePatchModel(OpenApiModel model, RestResourceEntity entity) {
        if (!model.getComponents().getSchemas().containsKey(entity.getModelEntityName() + "PatchModel")) {
            var attributes = entity.getAttributes();
            var requestSchema = new OpenApiObjectDataType();
            LinkedHashMap<String, OpenApiSchema> attributesMap = linkedMapOf(null, null);
            for (var attribute : attributes) {
                if (!(attribute.getType().equals("CONTENT")) && !attribute.isNaturalId()) {
                    attributesMap.put(attribute.getModelAttributeName(), OpenApiDataTypes.of(attribute.getType()));
                }
            }
            var relations = entity.getRelations();
            for (var relation: relations) {
                if (!(relation.isManyTargetPerSource())) {
                    attributesMap.put(relation.getRestRelationName(), OpenApiDataTypes.STRING);
                }
            }
            requestSchema.getProperties().putAll(attributesMap);
            model.getComponents().getSchemas().put(entity.getModelEntityName() + "PatchModel", requestSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + entity.getModelEntityName() + "PatchModel");
    }

    private OpenApiReferenceObject createOrReferenceResponseModel(OpenApiModel model, RestResourceEntity entity) {
        // figure out how to represent the entity attributes in swagger
        // if it already exists, just get the name
        // otherwise, add it first to model.components, before returning the reference

        if (!model.getComponents().getSchemas().containsKey(entity.getModelEntityName() + "Response")) {
            var attributes = entity.getAttributes();
            var entitySchema = new OpenApiObjectDataType();
            LinkedHashMap<String, OpenApiSchema> attributesMap = linkedMapOf(null, null);
            for (var attribute : attributes) {
                if (!(attribute.getType().equals("CONTENT"))) {
                    attributesMap.put(attribute.getModelAttributeName(), OpenApiDataTypes.of(attribute.getType()));
                } else {
                    attributesMap.put(attribute.getModelAttributeName() + "Id", OpenApiDataTypes.STRING);
                    attributesMap.put(attribute.getModelAttributeName() + "Length", OpenApiDataTypes.INTEGER);
                    attributesMap.put(attribute.getModelAttributeName() + "Mimetype", OpenApiDataTypes.STRING);
                    attributesMap.put(attribute.getModelAttributeName() + "Filename", OpenApiDataTypes.STRING);
                }
            }
            entitySchema.getProperties().putAll(attributesMap);
            LinkedHashMap<String, OpenApiSchema> linksMap = linkedMapOf(
                    "self", new OpenApiReferenceObject("#/components/schemas/Link"),
                    entity.getItemResource().getRelationName(), new OpenApiReferenceObject("#/components/schemas/Link")
            );
            for (var attribute : attributes) {
                if (attribute.getType().equals("CONTENT")) {
                    linksMap.put(attribute.getModelAttributeName(), new OpenApiReferenceObject("#/components/schemas/Link"));
                }
            }
            var relations = entity.getRelations();
            for (var relation : relations) {
                linksMap.put(relation.getRestRelationName(), new OpenApiReferenceObject("#/components/schemas/Link"));
            }
            entitySchema.getProperties().putAll(linkedMapOf("_links", new OpenApiObjectDataType(linksMap)));
            model.getComponents().getSchemas().put(entity.getModelEntityName() + "Response", entitySchema);
        }

        // return a reference to the entity-model we just created
        return new OpenApiReferenceObject("#/components/schemas/" + entity.getModelEntityName() + "Response");
    }

    private OpenApiReferenceObject createOrReferenceCollectionModel(OpenApiModel model, RestResourceEntity entity) {
        // add collection item to model.components, before returning the reference
        if (!model.getComponents().getSchemas().containsKey(entity.getModelEntityName() + "Collection")) {
            var collectionSchema = new OpenApiObjectDataType();
            collectionSchema.getProperties().putAll(linkedMapOf(
                    "_embedded", new OpenApiObjectDataType(linkedMapOf(
                            entity.getPathSegment(), new OpenApiArrayDataType(createOrReferenceResponseModel(model, entity)))),
                    "_links", new OpenApiObjectDataType(linkedMapOf(
                            "self", new OpenApiReferenceObject("#/components/schemas/Link"),
                            "profile", new OpenApiReferenceObject("#/components/schemas/Link"))
                    ),
                    "page", new OpenApiReferenceObject("#/components/schemas/page")
            ));
            model.getComponents().getSchemas().put(entity.getModelEntityName() + "Collection", collectionSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + entity.getModelEntityName() + "Collection");
    }

    private void componentInitialization(OpenApiModel model) {
        if (!model.getComponents().getSchemas().containsKey("Link")) {
            var linkSchema = new OpenApiObjectDataType();
            linkSchema.getProperties().putAll(linkedMapOf("href", OpenApiDataTypes.STRING));
            model.getComponents().getSchemas().put("Link", linkSchema);
        }

        var pageSchema = new OpenApiObjectDataType();
        pageSchema.getProperties().putAll(linkedMapOf(
                "size", OpenApiDataTypes.INTEGER,
                "totalElements", OpenApiDataTypes.INTEGER,
                "totalPages", OpenApiDataTypes.INTEGER,
                "number", OpenApiDataTypes.INTEGER
        ));
        model.getComponents().getSchemas().put("page", pageSchema);
    }

    private <K,V> LinkedHashMap<K, V> linkedMapOf(K k1, V v1) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        return map;
    }
    private <K,V> LinkedHashMap<K, V> linkedMapOf(K k1, V v1, K k2, V v2) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }
    private <K,V> LinkedHashMap<K, V> linkedMapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }
    private <K,V> LinkedHashMap<K, V> linkedMapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

}