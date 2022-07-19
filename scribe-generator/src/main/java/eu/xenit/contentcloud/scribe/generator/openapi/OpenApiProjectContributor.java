package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.openapi.model.*;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiParameters.ParameterType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
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

    private final OpenApiYmlWriter openApiWriter = new OpenApiYmlWriter();

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

        model.getServers().add(new OpenApiServers("http://localhost:8000"));
        componentInitialization(model);

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

        // 2c. content attribute - GET /broker/143/summary
        this.contributeContentAttribute(entity, model);

        // 2d. association resource - GET /accountStateZips/143/insurer
        this.contributeAssociationResource(entity, model);
    }

    // Add all the collection paths
    // GET|HEAD|POST /{repository}/
    // For example: GET /accountStateZips?page=1&size=10
    private void contributeCollectionResources(Entity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        var pathGet = new OpenApiModelPath(tags, "get-" + modifiedName(entity.getName()));
        var attributes = entity.getAttributes();
        for (var attribute : attributes) {
            if (attribute.isIndexed()) {
                pathGet.getParameters().addAll(List.of(
                        new OpenApiParameters(attribute.getName(), ParameterType.QUERY, false, OpenApiDataTypes.of(attribute.getType()))
                ));
            }
        }

        var attributes = entity.getAttributes();
        for (var attribute : attributes) {
            if (attribute.isIndexed()) {
                pathGet.getParameters().addAll(List.of(
                        new OpenApiParameters(attribute.getName(), ParameterType.QUERY, false, OpenApiDataTypes.of(attribute.getType()))
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

        var pathPost = new OpenApiModelPath(tags, "create-" + modifiedName(entity.getName()));
        pathPost.setRequestBody(new OpenApiRequestBody(
                "Create " + entity.getName(), true,
                linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferencePostModel(model, entity))))
        );
        pathPost.getResponses().putAll(linkedMapOf(
                "201", new OpenApiResponse("Created", linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("post", pathPost);

        model.getPaths().put("/" + modifiedName(entity.getName()), pathMap);
    }

    private void contributeItemResources(Entity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        var pathGet = new OpenApiModelPath(tags, "get-" + StringUtils.uncapitalize(entity.getName()));
        pathGet.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
        ));
        pathGet.getResponses().putAll(linkedMapOf(
                "200", new OpenApiResponse("OK", linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("get", pathGet);

        var pathPut = new OpenApiModelPath(tags, "update-" + StringUtils.uncapitalize(entity.getName()));
        pathPut.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
        ));
        pathPut.setRequestBody(new OpenApiRequestBody(
                "Update " + entity.getName(), true,
                linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferencePutModel(model, entity))))
        );
        pathPut.getResponses().putAll(linkedMapOf(
                "200", new OpenApiResponse("OK", linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("put", pathPut);

        var pathPatch = new OpenApiModelPath(tags, "patch-" + StringUtils.uncapitalize(entity.getName()));
        pathPatch.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
        ));
        pathPatch.setRequestBody(new OpenApiRequestBody(
                "Partially update " + entity.getName(), true,
                linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferencePatchModel(model, entity))))
        );
        pathPatch.getResponses().putAll(linkedMapOf(
                "200", new OpenApiResponse("OK", linkedMapOf("application/json", new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("patch", pathPatch);

        var pathDelete = new OpenApiModelPath(tags, "delete-" + StringUtils.uncapitalize(entity.getName()));
        pathDelete.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
        ));
        pathDelete.getResponses().putAll(linkedMapOf(
                "204", new OpenApiResponse("No Content"),
                "405", new OpenApiResponse("Not Allowed")
        ));
        pathMap.put("delete", pathDelete);

        model.getPaths().put("/" + modifiedName(entity.getName()) + "/{id}", pathMap);
    }

    private void contributeContentAttribute(Entity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();
        var attributes = entity.getAttributes();

        for (var attribute : attributes) {
            if (attribute.getType().equals("CONTENT")) {
                var pathGet = new OpenApiModelPath(tags, "get-" + StringUtils.uncapitalize(attribute.getName()));
                pathGet.getParameters().addAll(List.of(
                        new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
                ));
                pathGet.getResponses().putAll(linkedMapOf(
                        "200", new OpenApiResponse("A file", linkedMapOf("*/*", new OpenApiMediaTypeObject(OpenApiDataTypes.CONTENT))),
                        "404", new OpenApiResponse("Not Found")
                ));
                pathMap.put("get", pathGet);

                var pathPut = new OpenApiModelPath(tags, "update-" + StringUtils.uncapitalize(attribute.getName()));
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

                var pathDelete = new OpenApiModelPath(tags, "delete-" + StringUtils.uncapitalize(attribute.getName()));
                pathDelete.getParameters().addAll(List.of(
                        new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
                ));
                pathDelete.getResponses().putAll(linkedMapOf(
                        "204", new OpenApiResponse("File deleted")
                ));
                pathMap.put("delete", pathDelete);

                model.getPaths().put("/" + modifiedName(entity.getName()) + "/{id}/" +
                        StringUtils.uncapitalize(attribute.getName()), pathMap);
            }
        }
    }

    private void contributeAssociationResource(Entity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getName());
        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();

        var relations = entity.getRelations();
        for (var relation : relations) {
            var pathGet = new OpenApiModelPath(tags, "get-" + StringUtils.uncapitalize(relation.getName()));
            pathGet.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
            ));
            pathGet.getResponses().putAll(linkedMapOf(
                    "200", new OpenApiResponse("OK", linkedMapOf("application/json",
                            new OpenApiMediaTypeObject(createOrReferenceResponseModel(model, entity)))),
                    "405", new OpenApiResponse("Not Allowed")
            ));
            pathMap.put("get", pathGet);

            var pathPut = new OpenApiModelPath(tags, "update-" + StringUtils.uncapitalize(relation.getName()));
            pathPut.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
            ));
            pathPut.getResponses().putAll(linkedMapOf(
                    "204", new OpenApiResponse("No Content"),
                    "400", new OpenApiResponse("Bad Request")
            ));
            pathPut.setRequestBody(new OpenApiRequestBody(
                    "Update " + relation.getName(), true,
                    linkedMapOf("text/uri-list", new OpenApiMediaTypeObject(new OpenApiRelationReference("/" + entity.getName() + "/5"))))
            );
            pathMap.put("put", pathPut);

            var pathPost = new OpenApiModelPath(tags, "create-" + StringUtils.uncapitalize(relation.getName()));
            pathPost.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
            ));
            pathPost.getResponses().putAll(linkedMapOf(
                    "204", new OpenApiResponse("No Content"),
                    "400", new OpenApiResponse("Bad Request")
                    ));
            pathPost.setRequestBody(new OpenApiRequestBody(
                    "Create " + relation.getName(), true,
                    linkedMapOf("text/uri-list", new OpenApiMediaTypeObject(new OpenApiRelationReference("/" + entity.getName() + "/5"))))
            );
            pathMap.put("post", pathPost);

            var pathDelete = new OpenApiModelPath(tags, "delete-" + StringUtils.uncapitalize(relation.getName()));
            pathDelete.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.STRING)
            ));
            pathDelete.getResponses().putAll(linkedMapOf(
                    "204", new OpenApiResponse("No Content"),
                    "405", new OpenApiResponse("Not Allowed")
            ));
            pathMap.put("delete", pathDelete);

            model.getPaths().put("/" + modifiedName(entity.getName()) + "/{id}/" +
                    StringUtils.uncapitalize(relation.getName()), pathMap);
        }
    }

    private OpenApiReferenceObject createOrReferencePostModel(OpenApiModel model, Entity entity) {
        if (!model.getComponents().getSchemas().containsKey(entity.getName() + "PostModel")) {
            var attributes = entity.getAttributes();
            var requestSchema = new OpenApiObjectDataType();
            LinkedHashMap<String, OpenApiSchema> attributesMap = linkedMapOf(null, null);
            for (var attribute : attributes) {
                if (!(attribute.getType().equals("CONTENT"))) {
                    attributesMap.put(StringUtils.uncapitalize(attribute.getName()), OpenApiDataTypes.of(attribute.getType()));
                }
                if (attribute.isRequired()) {
                    requestSchema.getRequired().add(attribute.getName());
                }
            }
            var relations = entity.getRelations();
            for (var relation: relations) {
                if (!(relation.isManyTargetPerSource())) {
                    attributesMap.put(StringUtils.uncapitalize(relation.getName()), OpenApiDataTypes.STRING);
                }
            }
            requestSchema.getProperties().putAll(attributesMap);
            model.getComponents().getSchemas().put(entity.getName() + "PostModel", requestSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + entity.getName() + "PostModel");
    }

    private OpenApiReferenceObject createOrReferencePutModel(OpenApiModel model, Entity entity) {
        if (!model.getComponents().getSchemas().containsKey(entity.getName() + "PutModel")) {
            var attributes = entity.getAttributes();
            var requestSchema = new OpenApiObjectDataType();
            LinkedHashMap<String, OpenApiSchema> attributesMap = linkedMapOf(null, null);
            for (var attribute : attributes) {
                if (!(attribute.getType().equals("CONTENT")) && !attribute.isNaturalId()) {
                    attributesMap.put(StringUtils.uncapitalize(attribute.getName()), OpenApiDataTypes.of(attribute.getType()));
                }
                if (attribute.isRequired() && !attribute.isNaturalId()) {
                    requestSchema.getRequired().add(attribute.getName());
                }
            }
            var relations = entity.getRelations();
            for (var relation: relations) {
                if (!(relation.isManyTargetPerSource())) {
                    attributesMap.put(StringUtils.uncapitalize(relation.getName()), OpenApiDataTypes.STRING);
                }
            }
            requestSchema.getProperties().putAll(attributesMap);
            model.getComponents().getSchemas().put(entity.getName() + "PutModel", requestSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + entity.getName() + "PutModel");
    }

    private OpenApiReferenceObject createOrReferencePatchModel(OpenApiModel model, Entity entity) {
        if (!model.getComponents().getSchemas().containsKey(entity.getName() + "PatchModel")) {
            var attributes = entity.getAttributes();
            var requestSchema = new OpenApiObjectDataType();
            LinkedHashMap<String, OpenApiSchema> attributesMap = linkedMapOf(null, null);
            for (var attribute : attributes) {
                if (!(attribute.getType().equals("CONTENT")) && !attribute.isNaturalId()) {
                    attributesMap.put(StringUtils.uncapitalize(attribute.getName()), OpenApiDataTypes.of(attribute.getType()));
                }
            }
            var relations = entity.getRelations();
            for (var relation: relations) {
                if (!(relation.isManyTargetPerSource())) {
                    attributesMap.put(StringUtils.uncapitalize(relation.getName()), OpenApiDataTypes.STRING);
                }
            }
            requestSchema.getProperties().putAll(attributesMap);
            model.getComponents().getSchemas().put(entity.getName() + "PatchModel", requestSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + entity.getName() + "PatchModel");
    }

    private OpenApiReferenceObject createOrReferenceResponseModel(OpenApiModel model, Entity entity) {
        // figure out how to represent the entity attributes in swagger
        // if it already exists, just get the name
        // otherwise, add it first to model.components, before returning the reference

        if (!model.getComponents().getSchemas().containsKey(entity.getName() + "Response")) {
            var attributes = entity.getAttributes();
            var entitySchema = new OpenApiObjectDataType();
            LinkedHashMap<String, OpenApiSchema> attributesMap = linkedMapOf(null, null);
            for (var attribute : attributes) {
                if (!(attribute.getType().equals("CONTENT"))) {
                    attributesMap.put(StringUtils.uncapitalize(attribute.getName()), OpenApiDataTypes.of(attribute.getType()));
                } else {
                    attributesMap.put(StringUtils.uncapitalize(attribute.getName()) + "Id", OpenApiDataTypes.STRING);
                    attributesMap.put(StringUtils.uncapitalize(attribute.getName()) + "Length", OpenApiDataTypes.INTEGER);
                    attributesMap.put(StringUtils.uncapitalize(attribute.getName()) + "Mimetype", OpenApiDataTypes.STRING);
                    attributesMap.put(StringUtils.uncapitalize(attribute.getName()) + "Filename", OpenApiDataTypes.STRING);
                }
            }
            entitySchema.getProperties().putAll(attributesMap);
            LinkedHashMap<String, OpenApiSchema> linksMap = linkedMapOf(
                    "self", new OpenApiReferenceObject("#/components/schemas/Link"),
                    StringUtils.uncapitalize(entity.getName()), new OpenApiReferenceObject("#/components/schemas/Link")
            );
            for (var attribute : attributes) {
                if (attribute.getType().equals("CONTENT")) {
                    linksMap.put(StringUtils.uncapitalize(attribute.getName()), new OpenApiReferenceObject("#/components/schemas/Link"));
                }
            }
            var relations = entity.getRelations();
            for (var relation : relations) {
                linksMap.put(StringUtils.uncapitalize(relation.getName()), new OpenApiReferenceObject("#/components/schemas/Link"));
            }
            entitySchema.getProperties().putAll(linkedMapOf("_links", new OpenApiObjectDataType(linksMap)));
            model.getComponents().getSchemas().put(entity.getName() + "Response", entitySchema);
        }

        // return a reference to the entity-model we just created
        return new OpenApiReferenceObject("#/components/schemas/" + entity.getName() + "Response");
    }

    private OpenApiReferenceObject createOrReferenceCollectionModel(OpenApiModel model, Entity entity) {
        // add collection item to model.components, before returning the reference
        if (!model.getComponents().getSchemas().containsKey(modifiedName(entity.getName()) + "Collection")) {
            var collectionSchema = new OpenApiObjectDataType();
            collectionSchema.getProperties().putAll(linkedMapOf(
                    "_embedded", new OpenApiObjectDataType(linkedMapOf(
                            modifiedName(entity.getName()), new OpenApiArrayDataType(createOrReferenceResponseModel(model, entity)))),
                    "_links", new OpenApiObjectDataType(linkedMapOf(
                            "self", new OpenApiReferenceObject("#/components/schemas/Link"),
                            "profile", new OpenApiReferenceObject("#/components/schemas/Link"))
                    ),
                    "page", new OpenApiReferenceObject("#/components/schemas/page")
            ));
            model.getComponents().getSchemas().put(modifiedName(entity.getName()) + "Collection", collectionSchema);
        }

        return new OpenApiReferenceObject("#/components/schemas/" + modifiedName(entity.getName()) + "Collection");
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

    private static String modifiedName(String myString) {
        return English.plural(StringUtils.uncapitalize(myString));
    }
}