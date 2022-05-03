package eu.xenit.contentcloud.scribe.generator.source.types;

import java.util.Objects;
import java.util.Optional;

public class DefaultDataTypeResolver implements DataTypeResolver {

    public Optional<SemanticType> resolve(String type) {
        if (Objects.equals(type, "String") || Objects.equals(type, "STRING")) {
            return Optional.of(SemanticType.STRING);
        }
//
//        if (Objects.equals(type, "DATETIME")) {
//            return ClassName.get(Instant.class);
//        }

        return Optional.empty();
    }

}
