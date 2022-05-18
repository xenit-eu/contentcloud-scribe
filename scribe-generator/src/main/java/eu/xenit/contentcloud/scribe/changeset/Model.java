package eu.xenit.contentcloud.scribe.changeset;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Value;

@Value
public class Model {
    List<Entity> entities;

    public Optional<Entity> getEntity(String name) {
        return entities.stream()
                .filter(entity -> Objects.equals(entity.getName(), name))
                .findFirst();
    }
}
