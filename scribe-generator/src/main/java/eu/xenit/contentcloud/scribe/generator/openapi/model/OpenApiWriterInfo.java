package eu.xenit.contentcloud.scribe.generator.openapi.model;

public class OpenApiWriterInfo {
    private String description;
    private String version;
    private String title;

    public OpenApiWriterInfo(String description, String version, String title) {
        this.description = description;
        this.version = version;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
