package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import lombok.Data;

@Data
public class EntityTypeName implements SemanticType {

    private final Entity entity;

}
