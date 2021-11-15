package eu.xenit.contentcloud.scribe.generator.source;

import eu.xenit.contentcloud.scribe.generator.source.jpa.JpaEntity;

public interface SourceGenerator {

    JpaEntity createJpaEntity(String name);

}
