package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.SourceFile;

/**
 * Strategy interface used to create a {@link SourceFile} from a {@link JpaEntity},
 * without specifying a JVM language or a dependency on a specific code-generator library.
 */
public interface JpaEntitySourceCodeGenerator {
    SourceFile createSourceFile(JpaEntity model);
}
