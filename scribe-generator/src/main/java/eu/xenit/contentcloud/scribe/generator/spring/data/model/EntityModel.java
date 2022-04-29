package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.scribe.changeset.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EntityModel {

    private final List<Entity> entities = new ArrayList<>();

    public EntityModel(List<Entity> entities) {
        this.entities.addAll(entities);
    }

    public Collection<Entity> entities() {
        return Collections.unmodifiableCollection(this.entities);
    }
}
