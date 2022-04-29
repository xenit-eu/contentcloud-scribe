package eu.xenit.contentcloud.scribe.generator.spring.content.source;

import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.ContentStore;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;

public interface SpringContentSourceCodeGenerator {

    SourceFile createSourceFile(ContentStore model);

}

