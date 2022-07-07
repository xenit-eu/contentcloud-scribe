package eu.xenit.contentcloud.scribe.generator.openapi.model;

public class OpenApiDataTypes {

    public static final OpenApiDataType STRING = new OpenApiDataType("string", null);
    public static final OpenApiDataType DATETIME = new OpenApiDataType("string", "date-time");
    public static final OpenApiDataType INTEGER = new OpenApiDataType("integer", null);
    public static final OpenApiDataType LONG = new OpenApiDataType("integer", "int64");
    public static final OpenApiDataType BOOLEAN = new OpenApiDataType("boolean", null);

    public static OpenApiDataType of(String type) {
        switch (type) {
            case "DATETIME":
                return DATETIME;
            case "INTEGER":
                return INTEGER;
            case "LONG":
                return LONG;
            case "BOOLEAN":
                return BOOLEAN;
            default:
                return STRING;
        }
    }

}
