package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import lombok.Value;

public interface RestResourceProperty {
    boolean isExported();
    String getModelPropertyName();
    String getPropertyName();
}

@Value
class RestResourcePropertyImpl implements RestResourceProperty {
    boolean exported;
    String modelPropertyName, propertyName;
}
