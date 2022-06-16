package eu.xenit.contentcloud.scribe.generator.source.types;

import lombok.Getter;
import lombok.NonNull;

public class CollectionType implements SemanticType {

    @Getter
    @NonNull
    private SemanticType elementType;

    private boolean ordered;

    CollectionType(SemanticType elementType, boolean ordered) {
        this.elementType = elementType;
        this.ordered = ordered;
    }

    public static CollectionType listOf(@NonNull SemanticType elementType) {
        return new CollectionType(elementType, true);
    }
}
