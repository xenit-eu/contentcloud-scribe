package eu.xenit.contentcloud.scribe.generator.spring.data;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SpringDataSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.java.SpringDataJavaSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.DefaultSpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class SpringDataProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    SpringDataPackageStructure packageStructure() {
        return new DefaultSpringDataPackageStructure(this.description.getPackageName());
    }

    @Bean
    SpringDataSourceCodeGenerator sourceGenerator(SpringDataPackageStructure packages) {
        Language language = this.description.getLanguage();
        if (language instanceof JavaLanguage) {
            return new SpringDataJavaSourceCodeGenerator((JavaLanguage) language, packages);
        }

        throw new UnsupportedOperationException(String.format("Language '%s' is not supported", language));
    }

    @Bean
    EntityModel entityModel() {
        var changeSet = this.description.getChangeset();
        if (changeSet == null) {
            return new EntityModel(List.of());
        }

        return new EntityModel(changeSet.getEntities());
    }

    @Bean
    public SpringDataEntityModelSourceCodeProjectContributor entityModelSourceCodeProjectContributor(EntityModel entityModel,
            SpringDataSourceCodeGenerator sourceGenerator) {
        return new SpringDataEntityModelSourceCodeProjectContributor(this.description, entityModel, sourceGenerator);
    }

    @Bean
    SpringDataRepositorySourceCodeProjectContributor repositoriesSourceCodeProjectContributor(EntityModel entityModel,
            SpringDataSourceCodeGenerator sourceGenerator) {
        return new SpringDataRepositorySourceCodeProjectContributor(this.description, entityModel, sourceGenerator);
    }

    @Bean
    ApplicationPropertiesCustomizer hibernateProperties() {
        return properties -> properties
                .put("spring.jpa.properties.hibernate.globally_quoted_identifiers", "true");
    }}
