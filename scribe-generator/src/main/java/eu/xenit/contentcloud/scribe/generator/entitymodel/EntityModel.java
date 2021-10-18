package eu.xenit.contentcloud.scribe.generator.entitymodel;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EntityModel {

    private final List<Entity> entities = new ArrayList<>();

    public EntityModel(List<Entity> entities) {
        this.entities.addAll(entities);
    }

    public Stream<Entity> entities() {
        return this.entities.stream();
    }
}
