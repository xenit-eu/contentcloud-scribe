package eu.xenit.contentcloud.scribe.generator.spring.content;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.SpringContentPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.content.source.SpringContentSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.content.source.java.SpringContentJavaSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
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
}
