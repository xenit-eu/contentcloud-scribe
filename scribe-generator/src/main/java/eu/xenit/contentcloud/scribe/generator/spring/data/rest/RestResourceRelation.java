package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import lombok.Value;

public interface RestResourceRelation {
    boolean isExported();
    String getModelRelationName();
    String getRestRelationName();
    ResourceURITemplate getUriTemplate();
}

@Value
class RestResourceRelationImpl implements RestResourceRelation {
    boolean exported;
    String modelRelationName;
    String restRelationName;
    ResourceURITemplate uriTemplate;
}
