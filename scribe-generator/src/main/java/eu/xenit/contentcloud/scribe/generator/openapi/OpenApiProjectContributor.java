package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.openapi.OpenApiWriterParameters.ParameterType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

        Map<String, Map<String, OpenApiModelPaths>> pathsMap = null;
        Map<String, OpenApiWriterResponse> responsesMap = new HashMap<>();

        var model = new OpenApiModel("2.0", info);

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


        Map<String, OpenApiModelPaths> pathMap = new HashMap<>();
        var pathGet = new OpenApiModelPaths(tags);
        pathGet.getParameters().addAll(List.of(
                new OpenApiWriterParameters("page", ParameterType.QUERY, false, OpenApiDataTypes.INTEGER),
                new OpenApiWriterParameters("size", ParameterType.QUERY, false, OpenApiDataTypes.INTEGER),
                new OpenApiWriterParameters("sort", ParameterType.QUERY, false, OpenApiDataTypes.STRING)
        ));
        pathGet.getResponses().putAll(Map.of(
            "200", new OpenApiWriterResponse(""),
                "405", new OpenApiWriterResponse("List is not allowed")
        ));
        pathMap.put("get", pathGet);

        var pathHead = new OpenApiModelPaths(tags);
        pathMap.put("head", pathHead);

        var pathPost = new OpenApiModelPaths(tags);
        pathMap.put("post", pathPost);



//        pathsMap = Map.of("/" + entity.getName(), pathMap);
        model.getPaths().put("/" + entity.getName(), pathMap);
    }
}
