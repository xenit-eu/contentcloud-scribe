package eu.xenit.contentcloud.scribe.generator.spring.data;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SimpleType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SpringDataSourceCodeGenerator;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class SpringDataEntityModelSourceCodeProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;

    private final EntityModel entityModel;

    private final SpringDataSourceCodeGenerator sourceGenerator;

    private final SpringDataPackageStructure packageStructure;

    private final DataTypeResolver dataTypeResolver;

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
        var jpaEntity = JpaEntity.withClassName(entity.getClassName());
        jpaEntity.lombokTypeAnnotations(lombok -> lombok
                .useGetter(this.description.useLombok())
                .useSetter(this.description.useLombok())
                .useNoArgsConstructor(this.description.useLombok()));

        entity.getAttributes().forEach(attribute -> {
            if ("CONTENT".equals(attribute.getType())) {
                jpaEntity.addProperty(SemanticType.STRING, attribute.getName() + "Id", property -> {
                    var contentId = SimpleType.get("org.springframework.content.commons.annotations", "ContentId");
                    property.addAnnotation(contentId);
                });

                jpaEntity.addProperty(SemanticType.NUMBER, attribute.getName() + "Length", property -> {
                    var contentLength = SimpleType.get("org.springframework.content.commons.annotations",
                            "ContentLength");
                    property.addAnnotation(contentLength);
                });

                jpaEntity.addProperty(SemanticType.STRING, attribute.getName() + "Mimetype", property -> {
                    var mimetype = SimpleType.get("org.springframework.content.commons.annotations", "MimeType");
                    property.addAnnotation(mimetype);
                });
            } else {
                var type = this.dataTypeResolver.resolve(attribute.getType())
                        .orElseThrow(() -> new RuntimeException("Could not resolve attribute type '"+attribute.getName()+"'"));
                jpaEntity.addProperty(type, attribute.getName());
            }
        });

        entity.getRelations().forEach(relation -> {
            var linkedEntity = entityModel.lookupEntity(relation.getTarget()).orElseThrow();
            var targetType = this.dataTypeResolver.resolve(linkedEntity.getName())
                    .orElseThrow(() -> new RuntimeException("Could not resolve relation type '"+linkedEntity.getName()+"'"));

            if (relation.isManySourcePerTarget()) {
                if (relation.isManyTargetPerSource()) {
                    // many-to-many

                } else {
                    // many-to-one
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

        return this.sourceGenerator.createSourceFile(jpaEntity);
    }
}
