package eu.xenit.contentcloud.scribe.changeset;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Model {
    @Singular
    @NonNull
    List<Entity> entities;

    public Optional<Entity> getEntity(String name) {
        return entities.stream()
                .filter(entity -> Objects.equals(entity.getName(), name))
                .findFirst();
    }

    public Optional<Attribute> getEntityAttribute(String entityName, String attributeName) {
        return getEntity(entityName).flatMap(e -> e.getAttribute(attributeName));
    }
}
