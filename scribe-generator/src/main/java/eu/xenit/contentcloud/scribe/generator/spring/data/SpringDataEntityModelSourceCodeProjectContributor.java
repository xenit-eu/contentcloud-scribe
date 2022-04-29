package eu.xenit.contentcloud.scribe.generator.spring.data;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SimpleType;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SpringDataSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class SpringDataEntityModelSourceCodeProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;

    private final EntityModel entityModel;

    private final SpringDataSourceCodeGenerator sourceGenerator;

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
                jpaEntity.addProperty(String.class, attribute.getName() + "Id", property -> {
                    var contentId= SimpleType.get("org.springframework.content.commons.annotations", "ContentId");
                    property.addAnnotation(contentId);
                });

                jpaEntity.addProperty(long.class, attribute.getName() + "Length", property -> {
                    var contentLength = SimpleType.get("org.springframework.content.commons.annotations",
                            "ContentLength");
                    property.addAnnotation(contentLength);
                });

                jpaEntity.addProperty(String.class, attribute.getName() + "Mimetype", property -> {
                    var mimetype = SimpleType.get("org.springframework.content.commons.annotations", "MimeType");
                    property.addAnnotation(mimetype);
                });
            } else {
                Type resolvedAttributeType = this.resolveAttributeType(attribute.getType());
                jpaEntity.addProperty(resolvedAttributeType, attribute.getName());
            }
        });

        return this.sourceGenerator.createSourceFile(jpaEntity);
    }

    private Type resolveAttributeType(String type) {
        if (Objects.equals(type, "String") || Objects.equals(type, "STRING")) {
            return String.class;
        }

        if (Objects.equals(type, "DATETIME")) {
            return Instant.class;
        }

        throw new IllegalArgumentException("cannot resolve data type: " + type);
    }


}
