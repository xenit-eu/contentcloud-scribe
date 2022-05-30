package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiDataTypes;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiMediaTypeObject;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiModel;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiModelPath;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiReferenceObject;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiRequestBody;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiWriterInfo;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiWriterParameters;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiWriterParameters.ParameterType;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiWriterResponse;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiWriterTags;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

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
        OpenApiWriterInfo info = new OpenApiWriterInfo("broker-content", "1.0.6", "Maelstrom Money");

        List<OpenApiWriterTags> tagList = new ArrayList<>();
        List<String> tags = Arrays.asList("");

        Map<String, Map<String, OpenApiModelPath>> pathsMap = null;
        Map<String, OpenApiWriterResponse> responsesMap = new HashMap<>();

        var model = new OpenApiModel("3.0.2", info);

        for (Entity entity : entityModel.entities()) {
            this.contributeEntityToOpenApiModel(entity, model);
        }

        return model;
    }

    private void contributeEntityToOpenApiModel(Entity entity, OpenApiModel model) {

        // 1. add the entity as a tag
        OpenApiWriterTags entityTag = new OpenApiWriterTags(entity.getName());
        model.getTags().add(entityTag);

        // 2. add all the paths for the entity
        // 2a. collections resource - GET or POST /accountStateZips
        this.contributeCollectionResources(entity, model);
        // 2b. item resource - GET /accountStateZips/143

        // 2c. association resource - GET /accountStateZips/143/insurer

    }

    // Add all the collection paths
    // GET|HEAD|POST /{repository}/
    // For example: GET /accountStateZips?page=1&size=10
    private void contributeCollectionResources(Entity entity, OpenApiModel model) {
        List<String> tags = List.of(entity.getName());

        Map<String, OpenApiModelPath> pathMap = new LinkedHashMap<>();
        var pathGet = new OpenApiModelPath(tags);
        pathGet.getParameters().addAll(List.of(
                new OpenApiWriterParameters("page", ParameterType.QUERY, false, OpenApiDataTypes.INTEGER),
                new OpenApiWriterParameters("size", ParameterType.QUERY, false, OpenApiDataTypes.INTEGER),
                new OpenApiWriterParameters("sort", ParameterType.QUERY, false, OpenApiDataTypes.STRING)
        ));
        pathGet.getResponses().putAll(Map.of(
                "200", new OpenApiWriterResponse("" /* TODO component model */),
                "405", new OpenApiWriterResponse("List is not supported")
        ));
        pathMap.put("get", pathGet);

        var pathHead = new OpenApiModelPath(tags);
        pathHead.getResponses().putAll(Map.of(
                "204", new OpenApiWriterResponse(""),
                "404", new OpenApiWriterResponse("")
        ));
        pathMap.put("head", pathHead);

        var pathPost = new OpenApiModelPath(tags);
        pathPost.setRequestBody(new OpenApiRequestBody(
                "Create " + entity.getName(), true,
                Map.of("application/json", new OpenApiMediaTypeObject(createOrReferenceModel(model, entity))))
        );
        pathMap.put("post", pathPost);

        model.getPaths().put("/" + entity.getName(), pathMap);
    }

    private OpenApiReferenceObject createOrReferenceModel(OpenApiModel model, Entity entity) {
        // figure out how to represent the entity attributes in swagger
        // if it already exists, just get the name

        // TODO lookup 'enitity' in model.components

        // otherwise, add it first to model.components, before returning the reference

        // TODO or create 'entity' in model.components

        // return a reference to the entity-model we just created
        return new OpenApiReferenceObject("#/components/schemas/"+ entity.getName());
    }
}
