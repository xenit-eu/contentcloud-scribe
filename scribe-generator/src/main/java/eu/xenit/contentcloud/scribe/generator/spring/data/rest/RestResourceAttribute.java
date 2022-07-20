package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import lombok.Value;

public interface RestResourceAttribute extends RestResourceEntityComponent {
    boolean isSearchable();
    String getModelAttributeName();
    String getRestAttributeName();
}

@Value
class RestResourceAttributeImpl implements RestResourceAttribute {
    boolean exported;
    boolean searchable;
    String modelAttributeName, restAttributeName;
}
