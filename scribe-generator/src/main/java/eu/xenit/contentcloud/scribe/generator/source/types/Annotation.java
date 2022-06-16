package eu.xenit.contentcloud.scribe.generator.source.types;

import java.lang.module.Configuration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Annotation {

    static Annotation withType(@NonNull SemanticType type) {
        return new DefaultAnnotation(type);
    }

    SemanticType getType();
    Map<String, Object> getMembers();

    default Annotation withMembers(Consumer<Map<String, Object>> callback) {
        callback.accept(this.getMembers());
        return this;
    }
}

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DefaultAnnotation implements Annotation {

    @Getter
    @NonNull
    private final SemanticType type;

    @Getter
    private final Map<String, Object> members = new LinkedHashMap<>();

}




