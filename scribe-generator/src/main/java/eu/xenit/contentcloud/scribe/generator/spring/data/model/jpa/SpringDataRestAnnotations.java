package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface SpringDataRestAnnotations {

    SpringDataRestAnnotation RestResource = new SpringDataRestAnnotation("RestResource");

    @ToString
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class SpringDataRestAnnotation implements SemanticType {

        @Getter
        private final String name;
    }
}
