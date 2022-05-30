package eu.xenit.contentcloud.scribe.generator.openapi.model;

public class OpenApiWriterResponse {

    private String description;
    private Object content;

    public OpenApiWriterResponse(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
