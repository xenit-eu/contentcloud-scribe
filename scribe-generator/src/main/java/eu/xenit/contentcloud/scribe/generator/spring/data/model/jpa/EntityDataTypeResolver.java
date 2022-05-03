package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityDataTypeResolver implements DataTypeResolver {

    @NonNull
    private final EntityModel entityModel;

    @Override
    public Optional<SemanticType> resolve(String type) {
        return this.entityModel.lookupEntity(type).map(EntityTypeName::new);
    }
}
