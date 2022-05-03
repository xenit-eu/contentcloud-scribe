package eu.xenit.contentcloud.scribe.generator.spring.data;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolverRegistry;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolverRegistry;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaBuiltInTypeResolver;
import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.source.types.DefaultDataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.DefaultSpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.EntityDataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SpringDataSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.java.JavaEntityTypeNameResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.java.SpringDataJavaSourceCodeGenerator;
import io.spring.initializr.generator.condition.ConditionalOnLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class SpringDataProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    SpringDataPackageStructure packageStructure() {
        return new DefaultSpringDataPackageStructure(this.description.getPackageName());
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
    DataTypeResolver defaultDataTypeResolver() {
        return new DefaultDataTypeResolver();
    }

    @Bean
    EntityDataTypeResolver entityDataTypeResolver(EntityModel entityModel) {
        return new EntityDataTypeResolver(entityModel);
    }

    @Bean
    @Primary
    DataTypeResolver dataTypeResolverRegistry(ObjectProvider<DataTypeResolver> resolvers) {
        return new DataTypeResolverRegistry(resolvers.stream().collect(Collectors.toUnmodifiableList()));
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> javaSemanticTypeResolver() {
        return new JavaBuiltInTypeResolver();
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> javaEntityTypeResolver(SpringDataPackageStructure packageStructure) {
        return new JavaEntityTypeNameResolver(packageStructure);
    }

    @Bean
    @Primary
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> semanticTypeResolverRegistry(ObjectProvider<SemanticTypeResolver<JavaTypeName>> resolvers) {
        return new SemanticTypeResolverRegistry<>(resolvers.stream().collect(Collectors.toUnmodifiableList()));
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SpringDataSourceCodeGenerator sourceGenerator(SpringDataPackageStructure packages,
            SemanticTypeResolver<JavaTypeName> typeResolver) {
        return new SpringDataJavaSourceCodeGenerator((JavaLanguage) this.description.getLanguage(),
                packages, typeResolver);
    }

    @Bean
    public SpringDataEntityModelSourceCodeProjectContributor entityModelSourceCodeProjectContributor(
            EntityModel entityModel, SpringDataSourceCodeGenerator sourceGenerator,
            SpringDataPackageStructure packageStructure, DataTypeResolver dataTypeResolver) {
        return new SpringDataEntityModelSourceCodeProjectContributor(this.description, entityModel, sourceGenerator,
                packageStructure, dataTypeResolver);
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
    }
}
