package eu.xenit.contentcloud.scribe.generator.source.types;

import java.util.Objects;
import java.util.Optional;

public class DefaultDataTypeResolver implements DataTypeResolver {

    public Optional<SemanticType> resolve(String type) {
        if (Objects.equals(type, "String") || Objects.equals(type, "STRING")) {
            return Optional.of(SemanticType.STRING);
        }

        if (Objects.equals(type, "LONG") || Objects.equals(type, "NUMBER")) {
            return Optional.of(SemanticType.NUMBER);
        }

        if (Objects.equals(type, "DATETIME") || Objects.equals(type, "TIMESTAMP")) {
            return Optional.of(SemanticType.TIMESTAMP);
        }

        if (Objects.equals(type, "BOOLEAN")) {
            return Optional.of(SemanticType.BOOLEAN);
        }

        if (Objects.equals(type, "UUID")) {
            return Optional.of(SemanticType.UUID);
        }

        return Optional.empty();
    }

}
