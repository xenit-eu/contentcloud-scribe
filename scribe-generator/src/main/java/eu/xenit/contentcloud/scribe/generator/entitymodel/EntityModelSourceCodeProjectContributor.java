package eu.xenit.contentcloud.scribe.generator.entitymodel;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.service.DefaultPackageStructure;
import eu.xenit.contentcloud.scribe.generator.source.java.JavaSourceGenerator;
import eu.xenit.contentcloud.scribe.generator.source.SourceGenerator;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

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
        DefaultPackageStructure packages = new DefaultPackageStructure(this.description);

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
