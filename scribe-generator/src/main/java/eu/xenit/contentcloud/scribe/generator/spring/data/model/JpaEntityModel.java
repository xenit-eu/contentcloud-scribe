package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityFactory;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaEntityModel {
    private final Map<Entity, JpaEntity> entities;

    public static JpaEntityModel fromModel(EntityModel model, JpaEntityFactory factory) {
        var entities = model.entities().stream()
                .collect(Collectors.toMap(Function.identity(), factory::createJpaEntity));
        return new JpaEntityModel(entities);
    }

    public Collection<JpaEntity> entities() {
        return entities.values();
    }

    public Collection<JpaRepository> repositories() {
        return entities().stream()
                .map(JpaRepository::forEntity)
                .toList();
    }

    public Optional<JpaEntity> find(Entity entity) {
        return Optional.ofNullable(entities.get(entity));
    }

    public Optional<JpaEntity> findByClassname(String className) {
        return entities()
                .stream()
                .filter(jpaEntity -> Objects.equals(jpaEntity.className(), className))
                .findAny();
    }

    public Optional<JpaEntity> findByEntityName(String entityName) {
        return entities()
                .stream()
                .filter(jpaEntity -> Objects.equals(jpaEntity.entityName(), entityName))
                .findAny();
    }

}
