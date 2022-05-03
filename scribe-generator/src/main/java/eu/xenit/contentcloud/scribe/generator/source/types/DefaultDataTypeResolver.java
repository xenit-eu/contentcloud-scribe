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

        if (Objects.equals(type, "DATETIME")) {
            return Optional.of(SemanticType.POINT_IN_TIME);
        }

        return Optional.empty();
    }

}
