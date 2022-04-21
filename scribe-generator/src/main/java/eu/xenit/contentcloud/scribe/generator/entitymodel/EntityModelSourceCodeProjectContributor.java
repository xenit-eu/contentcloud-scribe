package eu.xenit.contentcloud.scribe.generator.entitymodel;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.service.DefaultPackageStructure;
import eu.xenit.contentcloud.scribe.generator.service.PackageStructure;
import eu.xenit.contentcloud.scribe.generator.source.java.JavaSourceGenerator;
import eu.xenit.contentcloud.scribe.generator.source.SourceGenerator;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import lombok.NonNull;
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
        Language language = this.description.getLanguage();

        PackageStructure packages = new DefaultPackageStructure(this.description);
        SourceGenerator sourceGen = createSourceGenerator(language, packages);
        SourceStructure mainSource = this.description.getBuildSystem().getMainSource(projectRoot, language);

        for (Entity entity : this.entityModel.entities()) {
            contributeEntity(mainSource, sourceGen, entity);
        }
    }

    private JavaSourceGenerator createSourceGenerator(@NonNull Language language, @NonNull PackageStructure packages) {
        if (language instanceof JavaLanguage) {
            return new JavaSourceGenerator((JavaLanguage) language, packages);
        }

        throw new UnsupportedOperationException(String.format("Language '%s' is not supported", language));
    }

    private void contributeEntity(SourceStructure mainSource,
            SourceGenerator sourceGenerator,
            Entity entity)
            throws IOException {

        var jpaEntity = sourceGenerator.createJpaEntity(entity.getClassName());
        jpaEntity.lombokTypeAnnotations(lombok -> lombok
                .useGetter(this.description.useLombok())
                .useSetter(this.description.useLombok())
                .useNoArgsConstructor(this.description.useLombok()));

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
