package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.SourceFile;

/**
 * Strategy interface used to create a {@link SourceFile} from a {@link JpaRepository},
 * without specifying a JVM language or a dependency on a specific code-generator library.
 */
public interface JpaRepositorySourceCodeGenerator {
    SourceFile createSourceFile(JpaRepository model);
}
