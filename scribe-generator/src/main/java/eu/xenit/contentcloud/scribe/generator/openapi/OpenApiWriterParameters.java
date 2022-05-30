package eu.xenit.contentcloud.scribe.generator.openapi;

public class OpenApiWriterParameters {
    private String name;
    private String in;
    private boolean required;
    private String type;

    public OpenApiWriterParameters(String name, String in, boolean required, String type) {
        this.name = name;
        this.in = in;
        this.required = required;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
