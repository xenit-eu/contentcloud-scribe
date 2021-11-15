package eu.xenit.contentcloud.scribe.generator.entitymodel;

import eu.xenit.contentcloud.bard.AnnotationSpec;
import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.FieldSpec;
import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.repository.RepositoryPackageStructure;
import eu.xenit.contentcloud.scribe.generator.source.java.JavaSourceGenerator;
import eu.xenit.contentcloud.scribe.generator.source.SourceGenerator;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class EntityModelSourceCodeProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;

    private final EntityModel entityModel;


    @Override
    public void contribute(Path projectRoot) throws IOException {
        SourceStructure mainSource = this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage());
        RepositoryPackageStructure packages = new RepositoryPackageStructure(this.description);

        SourceGenerator sourceGen = new JavaSourceGenerator(packages, this.description.useLombok());

        for (Entity entity : this.entityModel.entities()) {
            contributeEntity(mainSource, sourceGen, entity);
        }
    }

    private void contributeEntity(SourceStructure mainSource,
                                  SourceGenerator sourceGenerator,
                                  Entity entity)
            throws IOException {

        var jpaEntity = sourceGenerator.createJpaEntity(entity.getClassName());

        entity.getAttributes().forEach(attribute -> {
            Type resolvedAttributeType = this.resolveAttributeType(attribute.getType());
            jpaEntity.addProperty(resolvedAttributeType, attribute.getName());
        });

        jpaEntity.generate().writeTo(mainSource.getSourcesDirectory());
//        var source = Types.jpaEntity(entity.getClassName());
//        source.generate(sourceGen)
//                .writeTo(mainSource.getSourcesDirectory());


    }

    private void contributeEntity2(SourceStructure mainSource, RepositoryPackageStructure packages, Entity entity) throws IOException {
        var type = TypeSpec.classBuilder(entity.getClassName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("javax.persistence", "Entity"))
                .addAnnotation(AnnotationSpec
                        .builder(ClassName.get("javax.persistence", "Table"))
                        .addMember("name", "$S", entity.getTableName()).build())
                .addAnnotation(ClassName.get("lombok", "Getter"))
                .addAnnotation(ClassName.get("lombok", "Setter"))
                .addAnnotation(ClassName.get("lombok", "NoArgsConstructor"))

                .addField(this.getIdField());

        entity.getAttributes().forEach(attribute -> {
            var resolvedAttributeType = this.resolveAttributeType(attribute.getType());
            type.addField(resolvedAttributeType, attribute.getName(), Modifier.PRIVATE);
        });

        JavaFile.builder(packages.getModelPackageName(), type.build())
                .indent("\t")
                .build()
                .writeTo(mainSource.getSourcesDirectory());
    }

    private FieldSpec getIdField() {
        return FieldSpec.builder(UUID.class, "_id", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("javax.persistence", "Id")).build())
                .addAnnotation(AnnotationSpec.builder(ClassName.get("javax.persistence", "GeneratedValue"))
                        .addMember("strategy", "$T.$L",
                                ClassName.get("javax.persistence", "GenerationType"), "AUTO")
                        .build())
                .build();
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
