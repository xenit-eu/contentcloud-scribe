package eu.xenit.contentcloud.scribe.generator.source.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Annotation {

    static Builder builder(@NonNull SemanticType type) {
        return new Builder(type);
    }

    SemanticType getType();

    @Getter
    @RequiredArgsConstructor
    class Builder {

        private final SemanticType type;

        public Annotation build() {
            return new DefaultAnnotation(type);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class DefaultAnnotation implements Annotation {

        @Getter
        @NonNull
        private final SemanticType type;
    }
}




