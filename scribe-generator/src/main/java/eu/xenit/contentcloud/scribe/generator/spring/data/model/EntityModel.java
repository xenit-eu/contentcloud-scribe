package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.scribe.changeset.Entity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.NonNull;

public class EntityModel {

    private final List<Entity> entities = new ArrayList<>();

    public EntityModel(List<Entity> entities) {
        this.entities.addAll(entities);
    }

    public Collection<Entity> entities() {
        return Collections.unmodifiableCollection(this.entities);
    }

    public Optional<Entity> lookupEntity(@NonNull String type) {
        return this.entities.stream()
                .filter(entity -> type.equalsIgnoreCase(entity.getName()))
                .findFirst();
    }
}
