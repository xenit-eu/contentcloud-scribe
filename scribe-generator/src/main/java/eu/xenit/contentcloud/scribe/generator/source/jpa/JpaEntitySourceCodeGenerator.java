package eu.xenit.contentcloud.scribe.generator.source.jpa;

import eu.xenit.contentcloud.scribe.generator.source.SourceFile;

@FunctionalInterface
public interface JpaEntitySourceCodeGenerator {

    SourceFile createSourceFile(JpaEntity jpaEntity);

}
