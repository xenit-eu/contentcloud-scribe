package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface JpaAnnotations {

    JpaAnnotation OneToOne = new JpaAnnotation("OneToOne");
    JpaAnnotation OneToMany = new JpaAnnotation("OneToMany");
    JpaAnnotation ManyToOne = new JpaAnnotation("ManyToOne");
    JpaAnnotation JoinColumn = new JpaAnnotation("JoinColumn");



    @ToString
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class JpaAnnotation implements SemanticType {

        @Getter
        private final String name;
    }
}
