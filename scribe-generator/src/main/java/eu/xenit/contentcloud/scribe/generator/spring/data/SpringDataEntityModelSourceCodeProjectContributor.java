package eu.xenit.contentcloud.scribe.generator.spring.data;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityCustomizer;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SpringDataSourceCodeGenerator;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.spring.util.LambdaSafe;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class SpringDataEntityModelSourceCodeProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;

    private final EntityModel entityModel;

    private final SpringDataSourceCodeGenerator sourceGenerator;

    private final DataTypeResolver dataTypeResolver;

    private final ObjectProvider<JpaEntityCustomizer> jpaEntityCustomizers;

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Language language = this.description.getLanguage();

        SourceStructure mainSource = this.description.getBuildSystem().getMainSource(projectRoot, language);

        for (Entity entity : this.entityModel.entities()) {
            var source = generate(entity);
            source.writeTo(mainSource.getSourcesDirectory());
        }
    }

    private SourceFile generate(Entity entity) {
        var jpaEntity = JpaEntity.withName(entity.getClassName());
        jpaEntity.lombokTypeAnnotations(lombok -> lombok
                .useGetter(this.description.useLombok())
                .useSetter(this.description.useLombok())
                .useNoArgsConstructor(this.description.useLombok()));

        entity.getAttributes().forEach(attribute -> {
            var type = this.dataTypeResolver.resolve(attribute.getType())
                    .orElseThrow(() -> new RuntimeException("Could not resolve attribute type '" + attribute.getType()
                            + "' for attribute "+ attribute.getName()));
            jpaEntity.addProperty(type, attribute.getName());
        });

        entity.getRelations().forEach(relation -> {
            var linkedEntity = entityModel.lookupEntity(relation.getTarget()).orElseThrow();
            var targetType = this.dataTypeResolver.resolve(linkedEntity.getName())
                    .orElseThrow(() -> new RuntimeException(
                            "Could not resolve relation type '" + linkedEntity.getName() + "'"));

            if (relation.isManySourcePerTarget()) {
                if (relation.isManyTargetPerSource()) {
                    // many-to-many

                } else {
                    // many-to-one
                    jpaEntity.addManyToOneRelation(relation.getName(), targetType, manyToOne -> {
                        manyToOne.required(relation.isRequired());
                    });
                }
            } else {
                if (relation.isManyTargetPerSource()) {
                    // one-to-many
                } else {
                    // one-to-one
                    jpaEntity.addOneToOneRelation(relation.getName(), targetType, oneToOne -> {
                        // edit @OneToOne attributes here
                    });
                }
            }
        });

        // call out to all JpaEntityCustomizers
        this.customizeJpaEntity(jpaEntity);

        return this.sourceGenerator.createSourceFile(jpaEntity);
    }

    private void customizeJpaEntity(JpaEntity jpaEntity) {
        List<JpaEntityCustomizer> customizers = this.jpaEntityCustomizers.orderedStream().collect(Collectors.toList());
        LambdaSafe.callbacks(JpaEntityCustomizer.class, customizers, jpaEntity)
                .invoke((customizer) -> customizer.customize(jpaEntity));
    }
}
