package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface JacksonAnnotations {

    JacksonAnnotation JsonProperty = new JacksonAnnotation("JsonProperty");
    JacksonAnnotation JsonIgnore = new JacksonAnnotation("JsonIgnore");

    @ToString
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class JacksonAnnotation implements SemanticType {

        @Getter
        private final String name;
    }
}
