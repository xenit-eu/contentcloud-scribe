package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestResourceEntityModel {
    private final Map<String, RestResourceEntity> entities;

    public static RestResourceEntityModel fromModel(EntityModel model) {
        var entities = model.entities().stream()
                .collect(Collectors.toMap(Entity::getName, RestResourceEntity::forEntity));
        return new RestResourceEntityModel(entities);
    }

    public Collection<RestResourceEntity> entities() {
        return entities.values();
    }

    public Optional<RestResourceEntity> find(Entity entity) {
        return Optional.ofNullable(entities.get(entity.getName()));
    }

    public Optional<RestResourceEntity> find(JpaEntity entity) {
        return Optional.ofNullable(entities.get(entity.entityName()));
    }

}
