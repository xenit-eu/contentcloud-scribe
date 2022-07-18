package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import lombok.Getter;
import lombok.Value;
import org.atteo.evo.inflector.English;
import org.springframework.util.StringUtils;

public interface RestResourceEntity {

    /**
     * Flag indicating whether this resource is exported at all.
     *
     * @return {@literal true} if the resource is to be exported, {@literal false} otherwise.
     */
    default boolean exported() {
        return true;
    }

    /**
     * The path segment under which this resource is to be exported.
     *
     * @return A valid path segment.
     */
    String getPathSegment();

    CollectionResourceInformation getCollectionResource();

    ItemResourceInformation getItemResource();

    static RestResourceEntity forEntity(Entity entity) {
        return new RestResourceEntityImpl(entity.getName());
    }

    static RestResourceEntity forSpringDefaults(JpaEntity entity) {
        return new RestResourceEntityImpl(entity.className());
    }


    @Value
    class CollectionResourceInformation {

        String relationName;
        String description;
        ResourceURITemplate uriTemplate;
    }

    @Value
    class ItemResourceInformation {

        String relationName;
        String description;
        ResourceURITemplate uriTemplate;
    }
}

class RestResourceEntityImpl implements RestResourceEntity {

    @Getter
    private final ItemResourceInformation itemResource;

    @Getter
    private final CollectionResourceInformation collectionResource;

    @Getter
    private final String pathSegment;

    public RestResourceEntityImpl(String entityName) {
        String singularName = StringUtils.uncapitalize(entityName);
        String pluralName = English.plural(singularName);
        this.pathSegment = pluralName;

        var collectionURI = ResourceURITemplate.of(ResourceURIComponent.path(this.pathSegment));
        this.itemResource = new ItemResourceInformation(
                singularName,
                null,
                collectionURI.slash(ResourceURIComponent.variable("id"))
        );

        this.collectionResource = new CollectionResourceInformation(
                pluralName,
                null,
                collectionURI
        );
    }
}

