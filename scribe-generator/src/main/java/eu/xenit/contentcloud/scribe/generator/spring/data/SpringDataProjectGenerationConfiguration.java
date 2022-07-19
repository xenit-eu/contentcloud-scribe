package eu.xenit.contentcloud.scribe.generator.spring.data;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolverRegistry;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaCollectionTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolverRegistry;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaBuiltInTypeResolver;
import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import eu.xenit.contentcloud.scribe.generator.source.types.DataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.source.types.DefaultDataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.DefaultSpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JpaEntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityDataTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityCustomizer;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityFactory;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SpringDataSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.java.JacksonAnnotationTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.java.JavaEntityTypeNameResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.java.JpaAnnotationTypeResolver;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.java.SpringDataAnnotationTypeResolver;
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
    JpaEntityModel jpaEntityModel(EntityModel entityModel, DataTypeResolver dataTypeResolver, ObjectProvider<JpaEntityCustomizer> entityCustomizers) {
        return JpaEntityModel.fromModel(entityModel, new JpaEntityFactory(description, dataTypeResolver, entityModel, entityCustomizers));
    }

    @Bean
    RestResourceEntityModel restResourceEntityModel(EntityModel entityModel) {
        return RestResourceEntityModel.fromModel(entityModel);
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
    SemanticTypeResolver<JavaTypeName> javaBuiltInTypeResolver() {
        return new JavaBuiltInTypeResolver();
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> javaCollectionTypeResolver() {
        return new JavaCollectionTypeResolver();
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> javaEntityTypeResolver(SpringDataPackageStructure packageStructure) {
        return new JavaEntityTypeNameResolver(packageStructure);
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> jpaAnnotationTypeResolver() {
        return new JpaAnnotationTypeResolver();
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> jacksonAnnotationTypeResolver() {
        return new JacksonAnnotationTypeResolver();
    }

    @Bean
    @ConditionalOnLanguage(JavaLanguage.ID)
    SemanticTypeResolver<JavaTypeName> springDataRestAnnotationTypeResolver() {
        return new SpringDataAnnotationTypeResolver();
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
            JpaEntityModel entityModel, SpringDataSourceCodeGenerator sourceGenerator) {
        return new SpringDataEntityModelSourceCodeProjectContributor(this.description, entityModel, sourceGenerator);
    }

    @Bean
    SpringDataRepositorySourceCodeProjectContributor repositoriesSourceCodeProjectContributor(JpaEntityModel entityModel,
            SpringDataSourceCodeGenerator sourceGenerator) {
        return new SpringDataRepositorySourceCodeProjectContributor(this.description, entityModel, sourceGenerator);
    }

    @Bean
    ApplicationPropertiesCustomizer hibernateProperties() {
        return properties -> properties
                .put("spring.jpa.properties.hibernate.globally_quoted_identifiers", "true");
    }
}
