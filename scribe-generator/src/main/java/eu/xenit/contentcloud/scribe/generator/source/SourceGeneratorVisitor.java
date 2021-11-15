package eu.xenit.contentcloud.scribe.generator.source;

import eu.xenit.contentcloud.scribe.generator.source.jpa.JpaEntity;

public interface SourceGeneratorVisitor {
    SourceFile createSourceFile(JpaEntity jpaEntity);
}
