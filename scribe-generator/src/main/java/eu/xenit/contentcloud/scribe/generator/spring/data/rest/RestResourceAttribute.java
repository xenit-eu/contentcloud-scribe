package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import lombok.Value;

public interface RestResourceAttribute {
    boolean isExported();
    String getModelAttributeName();
    String getRestAttributeName();
}

@Value
class RestResourceAttributeImpl implements RestResourceAttribute {
    boolean exported;
    String modelAttributeName, restAttributeName;
}
