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
                        new OpenApiMediaTypeObject(createOrReferenceModel(model, entity)))),
                "405", new OpenApiResponse("List is not supported")
        ));
        pathMap.put("get", pathGet);

        // HEAD
        var pathHead = new OpenApiModelPath(tags);
        pathHead.getResponses().putAll(Map.of(
                "204", new OpenApiResponse(""),
                "404", new OpenApiResponse("")
        ));
        pathMap.put("head", pathHead);

        // POST
        var pathPost = new OpenApiModelPath(tags);
        pathPost.getResponses().putAll(Map.of(
                "204", new OpenApiResponse(""),
                "405", new OpenApiResponse("")
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
        pathGet.getResponses().putAll(Map.of(
                "200", new OpenApiResponse("OK", Map.of("application/json",
                        new OpenApiMediaTypeObject(createOrReferenceModel(model, entity)))),
                "405", new OpenApiResponse("")
        ));
        pathMap.put("get", pathGet);

        // HEAD
        var pathHead = new OpenApiModelPath(tags);
        pathHead.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
        pathHead.getResponses().putAll(Map.of(
                "204", new OpenApiResponse(""),
                "404", new OpenApiResponse("")
        ));
        pathMap.put("head", pathHead);

        // PUT
        var pathPut = new OpenApiModelPath(tags);
        pathPut.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
        pathPut.getResponses().putAll(Map.of(
                "204", new OpenApiResponse(""),
                "405", new OpenApiResponse("")
        ));
        pathMap.put("put", pathPut);

        // PATCH
        var pathPatch = new OpenApiModelPath(tags);
        pathPatch.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
        pathPatch.getResponses().putAll(Map.of(
                "405", new OpenApiResponse("")
        ));
        pathMap.put("patch", pathPatch);

        // DELETE
        var pathDelete = new OpenApiModelPath(tags);
        pathDelete.getParameters().addAll(List.of(
                new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
        ));
        pathDelete.getResponses().putAll(Map.of(
                "204", new OpenApiResponse(""),
                "405", new OpenApiResponse("")
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
            pathGet.getResponses().putAll(Map.of(
                    "200", new OpenApiResponse("OK", Map.of("application/json",
                            new OpenApiMediaTypeObject(createOrReferenceModel(model, entity)))),
                    "405", new OpenApiResponse("")
            ));
            pathMap.put("get", pathGet);

            // PUT
            var pathPut = new OpenApiModelPath(tags);
            pathPut.getParameters().addAll(List.of(
                    new OpenApiParameters("id", ParameterType.PATH, true, OpenApiDataTypes.INTEGER)
            ));
            pathPut.getResponses().putAll(Map.of(
                    "204", new OpenApiResponse(""),
                    "400", new OpenApiResponse("")
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
                    "204", new OpenApiResponse(""),
                    "400", new OpenApiResponse("")
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
                    "204", new OpenApiResponse(""),
                    "405", new OpenApiResponse("")
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
            entitySchema.getProperties().putAll(Map.of(
                    "name", OpenApiDataTypes.STRING,
                    "_links", new OpenApiObjectDataType(Map.of(
                            "self", new OpenApiReferenceObject("#/components/schemas/Link"),
                            StringUtils.uncapitalize(entity.getName()), new OpenApiReferenceObject("#/components/schemas/Link"))
            )));
            model.getComponents().getSchemas().put(entity.getName(), entitySchema);
        }

        if (!model.getComponents().getSchemas().containsKey("Link")) {
            var linkSchema = new OpenApiObjectDataType();
            linkSchema.getProperties().putAll(Map.of("href", OpenApiDataTypes.STRING));
            model.getComponents().getSchemas().put("Link", linkSchema);
        }

        // return a reference to the entity-model we just created
        return new OpenApiReferenceObject("#/components/schemas/" + entity.getName());
    }

    private static String modifiedName(String myString) {
        return English.plural(StringUtils.uncapitalize(myString));
    }
}