package eu.xenit.contentcloud.scribe.generator.spring.content;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.ContentDataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.ContentDataTypeResolver.ContentDataType;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.SpringContentAnnotations;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.SpringContentPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.content.source.SpringContentSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.content.source.java.SpringContentAnnotationDataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.content.source.java.SpringContentJavaSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityCustomizer;
import io.spring.initializr.generator.condition.ConditionalOnLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class SpringContentProjectionGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    ApplicationPropertiesCustomizer springContentDefaultStorage() {
        return properties -> {
            properties.put("spring.content.storage.type.default", "fs");

            properties.put("spring.servlet.multipart.max-file-size", "-1");
            properties.put("spring.servlet.multipart.max-request-size", "-1");
        };
    }

    @Bean
    SpringContentPackageStructure springContentPackageStructure() {
        return () -> description.getPackageName() + ".store";
    }

    @Bean
    ContentDataTypeResolver contentDataTypeResolver(EntityModel entityModel) {
        return new ContentDataTypeResolver();
    }

    @Bean
    SpringContentSourceCodeGenerator springContentJavaSourceCodeGenerator(SpringDataPackageStructure dataPkgStructure,
            SpringContentPackageStructure contentPackageStructure) {
        return new SpringContentJavaSourceCodeGenerator(dataPkgStructure, contentPackageStructure);
    }

    @Bean
    SpringContentStoreSourceCodeProjectContributor springContentStoreSourceCodeProjectContributor(
            EntityModel entityModel,
            SpringContentSourceCodeGenerator sourceGenerator) {
        return new SpringContentStoreSourceCodeProjectContributor(this.description, entityModel, sourceGenerator);
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> springContentAnnotationDataTypeResolver() {
        return new SpringContentAnnotationDataTypeResolver();
    }

    @Bean
    JpaEntityCustomizer contentAttributesJpaEntityCustomizer() {
        return new JpaEntityCustomizer() {
            @Override
            public void customize(JpaEntity jpaEntity) {
                // collect all the content-attributes
                var contentAttributes = jpaEntity.fields()
                        .filter(field -> field.type() instanceof ContentDataType)
                        .toList();

                // Replace/expand the CONTENT attribute into the following properties:
                // - @ContentId String/UUID <name>Id
                // - @ContentLength long <name>Length
                // - @Mimetype String mimetype
                // - @OriginalFilename String originalFilename
                contentAttributes.forEach(attr -> {
                    jpaEntity.removeProperty(attr.canonicalName());

                    jpaEntity.addProperty(SemanticType.STRING, attr.canonicalName() + "Id", field -> {
                            field.addAnnotation(SpringContentAnnotations.ContentId);
                    });
                    jpaEntity.addProperty(SemanticType.NUMBER, attr.canonicalName() + "Length", field -> {
                        field.addAnnotation(SpringContentAnnotations.ContentLength);
                    });
                    jpaEntity.addProperty(SemanticType.STRING, attr.canonicalName() + "Mimetype", field -> {
                        field.addAnnotation(SpringContentAnnotations.Mimetype);
                    });
                    jpaEntity.addProperty(SemanticType.STRING, attr.canonicalName() + "Filename", field -> {
                        field.addAnnotation(SpringContentAnnotations.OriginalFilename);
                    });
                });
            }
        };
    }
}
