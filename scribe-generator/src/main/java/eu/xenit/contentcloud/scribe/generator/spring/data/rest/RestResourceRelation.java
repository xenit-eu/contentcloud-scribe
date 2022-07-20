package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

public interface RestResourceRelation extends RestResourceEntityComponent {
    String getModelRelationName();
    String getRestRelationName();
    String getPathSegment();
    ResourceURITemplate getUriTemplate();

    @Override
    default Optional<RestResourceAttribute> asAttribute() {
        return Optional.of(new RestResourceAttribute() {
            @Override
            public boolean isExported() {
                return RestResourceRelation.this.isExported();
            }

            @Override
            public boolean isSearchable() {
                return false;
            }

            @Override
            public String getModelAttributeName() {
                return getModelRelationName();
            }

            @Override
            public String getRestAttributeName() {
                return getRestRelationName();
            }
        });
    }
}

@Value
class RestResourceRelationImpl implements RestResourceRelation {
    boolean exported;
    String modelRelationName;
    String restRelationName;
    String pathSegment;
    @Getter(value = AccessLevel.NONE)
    ResourceURITemplate baseUriTemplate;

    @Override
    public ResourceURITemplate getUriTemplate() {
        return baseUriTemplate.slash(ResourceURIComponent.path(pathSegment));
    }
}
