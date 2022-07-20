package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Relation;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityRelationship;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.ManyToOneRelation;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.OneToManyRelation;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.OneToOneRelation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
    default boolean isExported() {
        return true;
    }

    String getModelEntityName();

    /**
     * The path segment under which this resource is to be exported.
     *
     * @return A valid path segment.
     */
    String getPathSegment();

    CollectionResourceInformation getCollectionResource();

    ItemResourceInformation getItemResource();

    Collection<RestResourceAttribute> getAttributes();
    Optional<RestResourceAttribute> findAttribute(String name);
    default Optional<RestResourceAttribute> findAttribute(JpaEntityProperty property) {
        return findAttribute(property.name());
    }
    default Optional<RestResourceAttribute> findAttribute(Attribute attribute) {
        return findAttribute(attribute.getName());
    }
    Collection<RestResourceRelation> getRelations();
    Optional<RestResourceRelation> findRelation(String name);
    default Optional<RestResourceRelation> findRelation(JpaEntityRelationship relation) {
        return findRelation(relation.name());
    }
    default Optional<RestResourceRelation> findRelation(Relation relation) {
        return findRelation(relation.getName());
    }

    default Optional<? extends RestResourceEntityComponent> findComponent(String name) {
        var relation = findRelation(name);
        if(relation.isPresent()) {
            return relation;
        }
        return findAttribute(name);
    }

    default Optional<? extends RestResourceEntityComponent> findComponent(JavaBeanProperty property) {
        return findComponent(property.name());
    }

    static RestResourceEntity forEntity(Entity entity) {
        var restResourceEntity = new RestResourceEntityImpl(entity.getName(), RestResourceEntityImpl.dashifyName(entity.getName()));

        for (Attribute attribute : entity.getAttributes()) {
            restResourceEntity.addAttribute(attribute.getName(), RestResourceEntityImpl.dashifyName(attribute.getName()), attribute.isIndexed());
        }

        for (Relation relation : entity.getRelations()) {
            restResourceEntity.addRelation(relation.getName(), RestResourceEntityImpl.dashifyName(relation.getName()));
        }

        return restResourceEntity;
    }

    static RestResourceEntity forSpringDefaults(JpaEntity entity) {
        var restResourceEntity = new RestResourceEntityImpl(entity.entityName(), StringUtils.uncapitalize(entity.className()));

        entity.fields()
                .filter(field -> field instanceof JpaEntityProperty)
                .forEachOrdered(field -> {
                    restResourceEntity.addAttribute(field.name(), field.fieldName(), false);
                });
        entity.fields()
                .filter(field -> field instanceof JpaEntityRelationship)
                .forEachOrdered(field -> {
                    restResourceEntity.addRelation(field.name(), field.fieldName());
                });

        return restResourceEntity;
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
    private final String modelEntityName;

    @Getter
    private final String pathSegment;

    private final Map<String, RestResourceAttribute> attributes = new LinkedHashMap<>();

    private final Map<String, RestResourceRelation> relations = new LinkedHashMap<>();

    public RestResourceEntityImpl(String modelEntityName, String restEntityName) {
        this.modelEntityName = modelEntityName;
        String pluralName = English.plural(restEntityName);
        this.pathSegment = pluralName;

        var collectionURI = ResourceURITemplate.of(ResourceURIComponent.path(this.pathSegment));
        this.itemResource = new ItemResourceInformation(
                restEntityName,
                null,
                collectionURI.slash(ResourceURIComponent.variable("id"))
        );

        this.collectionResource = new CollectionResourceInformation(
                pluralName,
                null,
                collectionURI
        );
    }

    void addAttribute(String modelName, String propertyName, boolean searchable) {
        attributes.put(modelName, new RestResourceAttributeImpl(true, searchable, modelName, propertyName));
    }

    void addRelation(String modelName, String relationName) {
        relations.put(modelName, new RestResourceRelationImpl(true, modelName, relationName, relationName, itemResource.getUriTemplate()));
    }

    static String dashifyName(String name) {
        String dashedName = name
                // Replace
                .replaceAll("[A-Z][a-z]*", "-$0")
                .replaceAll("-+", "-");
        if(dashedName.startsWith("-")) {
            // Strip first dash from the name
            dashedName = dashedName.substring(1);
        }
        return dashedName.toLowerCase(Locale.ROOT);
    }

    @Override
    public Collection<RestResourceAttribute> getAttributes() {
        return attributes.values();
    }

    @Override
    public Optional<RestResourceAttribute> findAttribute(String name) {
        return Optional.ofNullable(attributes.get(name));
    }

    @Override
    public Collection<RestResourceRelation> getRelations() {
        return relations.values();
    }

    @Override
    public Optional<RestResourceRelation> findRelation(String name) {
        return Optional.ofNullable(relations.get(name));
    }
}

