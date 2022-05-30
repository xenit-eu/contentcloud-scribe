package eu.xenit.contentcloud.scribe.generator.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class OpenApiWriter {

    public void writeOpenApiSpec(BufferedWriter writer, EntityModel entityModel) throws IOException {
        PrintWriter out = new PrintWriter(writer);

        OpenApiWriterInfo info = new OpenApiWriterInfo("broker-content", "1.0.6", "Maelstrom Money");

        List<OpenApiWriterTags> tagList = new ArrayList<>();
        List<String> tags = Arrays.asList("");
        Map<String, Map<String, OpenApiWriterPaths>> pathsMap = null;

        Map<String, OpenApiWriterResponses> responsesMap = new HashMap<>();

        for (Entity entity : entityModel.entities()) {
            List<OpenApiWriterParameters> parameterList = new ArrayList<>();

            OpenApiWriterTags tag1 = new OpenApiWriterTags(entity.getName());
            tagList.add(tag1);

            tags = Arrays.asList(entity.getName());

            OpenApiWriterParameters parameters = new OpenApiWriterParameters("page", "query", true, "string");
            parameterList.add(parameters);

            OpenApiWriterResponses responses = new OpenApiWriterResponses("Invalid ID supplied");
            responsesMap.put("404", responses);

            OpenApiWriterPaths paths = new OpenApiWriterPaths(tags, parameterList, responsesMap);

            Map<String, OpenApiWriterPaths> pathMap = new HashMap<>();
            pathMap.put("post", paths);
            pathMap.put("put", paths);
            pathMap.put("get", paths);
            pathsMap = Map.of("/" + entity.getName(), pathMap);

        }
        OpenApiWriterContainer test = new OpenApiWriterContainer("2.0", info, tagList, pathsMap);

        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        om.writeValue(out, test);

//        List<OpenApiWriterTags> tagList = new ArrayList<>();
//
//        for (Entity entity : entityModel.entities()) {
//            OpenApiWriterTags tag1 = new OpenApiWriterTags(entity.getName());
//            tagList.add(tag1);
//        }
//
//        List<String> tags = new ArrayList<>();
//        tags.add("accountStateZip");
//
//        List<OpenApiWriterParameters> parameterList = new ArrayList<>();
//        OpenApiWriterParameters parameters = new OpenApiWriterParameters("page", "query", true, "string");
//        parameterList.add(parameters);
//
//        Map<String, OpenApiWriterResponses> responsesMap = new HashMap<>();
//        OpenApiWriterResponses responses = new OpenApiWriterResponses("Invalid ID supplied");
//        responsesMap.put("404", responses);
//
//        OpenApiWriterPaths paths = new OpenApiWriterPaths(tags, parameterList, responsesMap);
//    //        Map<String, OpenApiWriterPaths> pathsMap = Map.of("get", paths);
//        Map<String, OpenApiWriterPaths> pathMap = new HashMap<>();
//        pathMap.put("post", paths);
//        pathMap.put("put", paths);
//        pathMap.put("get", paths);
//        Map<String, Map<String, OpenApiWriterPaths>> pathsMap = Map.of("/accountStateZip", pathMap);
//
//        OpenApiWriterContainer test = new OpenApiWriterContainer("2.0", info, tagList, pathsMap);
//
//        ObjectMapper om = new ObjectMapper(new YAMLFactory());
//        om.writeValue(out, test);
    }
}