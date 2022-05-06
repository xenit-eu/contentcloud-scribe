package eu.xenit.contentcloud.scribe.generator.spring.content.model;

import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
public class ContentDataTypeResolver implements DataTypeResolver {

    private static final ContentDataType INSTANCE = new ContentDataType();

    @Override
    public Optional<SemanticType> resolve(String type) {
        if (Objects.equals("CONTENT", type)) {
            return Optional.of(INSTANCE);
        }

        return Optional.empty();
    }

    @ToString
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentDataType implements SemanticType {

    }
}
