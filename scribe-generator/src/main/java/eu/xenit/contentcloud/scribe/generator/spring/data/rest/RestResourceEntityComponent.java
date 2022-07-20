package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import java.util.Optional;

public interface RestResourceEntityComponent {
    boolean isExported();

    default Optional<RestResourceAttribute> asAttribute() {
        if(this instanceof RestResourceAttribute) {
            return Optional.of((RestResourceAttribute) this);
        }
        return Optional.empty();
    }

    default Optional<RestResourceRelation> asRelation() {
        if(this instanceof RestResourceRelation) {
            return Optional.of((RestResourceRelation) this);
        }
        return Optional.empty();
    }
}
