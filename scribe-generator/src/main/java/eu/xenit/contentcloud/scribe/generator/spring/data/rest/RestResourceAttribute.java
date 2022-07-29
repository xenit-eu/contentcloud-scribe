package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import lombok.Value;

public interface RestResourceAttribute extends RestResourceEntityComponent {
    boolean isSearchable();
    boolean isRequired();
    boolean isNaturalId();

    String getModelAttributeName();
    String getRestAttributeName();
    String getType();

}

@Value
class RestResourceAttributeImpl implements RestResourceAttribute {
    boolean exported;
    boolean searchable;
    String modelAttributeName, restAttributeName;
    String type;
    boolean required;
    boolean naturalId;
}
