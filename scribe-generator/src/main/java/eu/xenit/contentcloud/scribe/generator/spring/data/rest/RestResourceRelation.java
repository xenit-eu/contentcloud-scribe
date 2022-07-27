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
    boolean isManyTargetPerSource();
    boolean isManySourcePerTarget();

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
            public boolean isRequired() {
                return false;
            }

            @Override
            public boolean isNaturalId() {
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

            @Override
            public String getType() {
                return null;
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
    boolean manyTargetPerSource;
    boolean manySourcePerTarget;

    @Override
    public ResourceURITemplate getUriTemplate() {
        return baseUriTemplate.slash(ResourceURIComponent.path(pathSegment));
    }
}
