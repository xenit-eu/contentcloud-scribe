package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntity;
import io.spring.initializr.generator.spring.util.LambdaSafe;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;

@AllArgsConstructor
public class JpaEntityFactory {
    private final ScribeProjectDescription description;
    private final DataTypeResolver dataTypeResolver;
    private final EntityModel entityModel;
    private final ObjectProvider<JpaEntityCustomizer> jpaEntityCustomizers;


    public JpaEntity createJpaEntity(Entity entity) {
        var jpaEntity = JpaEntity.withName(entity.getName());
        jpaEntity.restResource(RestResourceEntity.forEntity(entity));
        jpaEntity.lombokTypeAnnotations(lombok -> lombok
                .useGetter(description.useLombok())
                .useSetter(description.useLombok())
                .useNoArgsConstructor(description.useLombok()));

        entity.getAttributes().forEach(attribute -> {
            var type = dataTypeResolver.resolve(attribute.getType())
                    .orElseThrow(() -> new RuntimeException("Could not resolve attribute type '" + attribute.getType()
                            + "' for attribute "+ attribute.getName()));
            jpaEntity.addProperty(type, attribute.getName());
        });

        entity.getRelations().forEach(relation -> {
            var linkedEntity = entityModel.lookupEntity(relation.getTarget()).orElseThrow();
            var targetType = dataTypeResolver.resolve(linkedEntity.getName())
                    .orElseThrow(() -> new RuntimeException(
                            "Could not resolve relation type '" + linkedEntity.getName() + "'"));

            if (relation.isManySourcePerTarget()) {
                if (relation.isManyTargetPerSource()) {
                    // many-to-many
                    jpaEntity.addManyToManyRelation(relation.getName(), targetType, manyToMany -> {

                    });
                } else {
                    // many-to-one
                    jpaEntity.addManyToOneRelation(relation.getName(), targetType, manyToOne -> {
                        manyToOne.required(relation.isRequired());
                    });
                }
            } else {
                if (relation.isManyTargetPerSource()) {
                    // one-to-many
                    jpaEntity.addOneToManyRelation(relation.getName(), targetType, oneToMany -> {

                    });
                } else {
                    // one-to-one
                    jpaEntity.addOneToOneRelation(relation.getName(), targetType, oneToOne -> {
                        oneToOne.required(relation.isRequired());
                    });
                }
            }
        });

        // call out to all JpaEntityCustomizers
        customizeJpaEntity(jpaEntity);
        return jpaEntity;
    }

    private void customizeJpaEntity(JpaEntityBuilder jpaEntity) {
        List<JpaEntityCustomizer> customizers = jpaEntityCustomizers.orderedStream().collect(
                Collectors.toList());
        LambdaSafe.callbacks(JpaEntityCustomizer.class, customizers, jpaEntity)
                .invoke((customizer) -> customizer.customize(jpaEntity));
    }
}
