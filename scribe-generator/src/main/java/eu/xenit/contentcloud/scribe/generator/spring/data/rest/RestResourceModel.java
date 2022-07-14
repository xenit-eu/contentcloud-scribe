package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import lombok.Getter;
import lombok.Value;
import org.atteo.evo.inflector.English;
import org.springframework.util.StringUtils;

public interface RestResourceModel {

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
    String pathSegment();

    CollectionResourceInformation getCollectionResource();

    ItemResourceInformation getItemResource();

    static RestResourceModel forEntity(Entity entity) {
        return new RestResourceModelImpl(entity);
    }


    @Value
    class CollectionResourceInformation {

        String relationName;
        String description;
        String uriTemplate;
    }

    @Value
    class ItemResourceInformation {

        String relationName;
        String description;
        String uriTemplate;
    }
}

class RestResourceModelImpl implements RestResourceModel {

    @Getter
    private final ItemResourceInformation itemResource;

    @Getter
    private final CollectionResourceInformation collectionResource;

    public RestResourceModelImpl(Entity entity) {
        this.itemResource = new ItemResourceInformation(
                StringUtils.uncapitalize(entity.getName()),
                null,
                "/{plural}/{id}"


        );
        this.collectionResource = new CollectionResourceInformation(
                English.plural(StringUtils.uncapitalize(entity.getName())),
                null,
                "/{plural}"
        );
    }

    @Override
    public String pathSegment() {
        return null;
    }
}

