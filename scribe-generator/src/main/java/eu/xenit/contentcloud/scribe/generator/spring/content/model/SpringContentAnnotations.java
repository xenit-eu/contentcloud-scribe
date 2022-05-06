package eu.xenit.contentcloud.scribe.generator.spring.content.model;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface SpringContentAnnotations {

    SpringContentAnnotation ContentId = new SpringContentAnnotation("ContentId");
    SpringContentAnnotation ContentLength = new SpringContentAnnotation("ContentLength");
    SpringContentAnnotation Mimetype = new SpringContentAnnotation("MimeType");
    SpringContentAnnotation OriginalFilename = new SpringContentAnnotation("OriginalFileName");

    @ToString
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class SpringContentAnnotation implements SemanticType {

        @Getter
        private final String name;
    }
}
