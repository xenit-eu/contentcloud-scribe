package eu.xenit.contentcloud.scribe.generator.source.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.model.SourceFile;
import eu.xenit.contentcloud.scribe.generator.source.model.TypeModel;

/**
 * Strategy interface used to create a {@link SourceFile} from a {@link TypeModel},
 * without specifying a JVM language or a dependency on a specific code-generator library.
 */
public interface TypeModelSourceCodeGenerator<T extends TypeModel> {

    SourceFile createSourceFile(T model);
}
