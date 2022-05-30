package eu.xenit.contentcloud.scribe.generator.openapi;

import java.util.List;
import java.util.Map;

public class OpenApiWriterContainer {
    private String swagger;
    private OpenApiWriterInfo info;
    private List<OpenApiWriterTags> tags;
    private Map<String, Map<String, OpenApiWriterPaths>> paths;

    public OpenApiWriterContainer(String swagger, OpenApiWriterInfo info, List<OpenApiWriterTags> tags, Map<String, Map<String, OpenApiWriterPaths>> paths) {
        this.swagger = swagger;
        this.info = info;
        this.tags = tags;
        this.paths = paths;
    }

    public String getSwagger() {
        return swagger;
    }

    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    public OpenApiWriterInfo getInfo() {
        return info;
    }

    public void setInfo(OpenApiWriterInfo info) {
        this.info = info;
    }

    public List<OpenApiWriterTags> getTags() {
        return tags;
    }

    public void setTags(List<OpenApiWriterTags> tags) {
        this.tags = tags;
    }

    public Map<String, Map<String, OpenApiWriterPaths>> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, Map<String, OpenApiWriterPaths>> paths) {
        this.paths = paths;
    }
}
