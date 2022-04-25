package eu.xenit.contentcloud.scribe.generator.source;

import eu.xenit.contentcloud.scribe.generator.source.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.JpaRepository;

public interface SourceGenerator {

    JpaEntity createJpaEntity(String entityClassName);

    JpaRepository createJpaRepository(String entityClassName);
}
