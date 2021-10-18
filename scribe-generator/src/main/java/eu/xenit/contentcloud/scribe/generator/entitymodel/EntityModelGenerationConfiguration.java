package eu.xenit.contentcloud.scribe.generator.entitymodel;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.java.JavaCompilationUnit;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.language.java.JavaSourceCodeWriter;
import io.spring.initializr.generator.language.java.JavaTypeDeclaration;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.MainCompilationUnitCustomizer;
import io.spring.initializr.generator.spring.code.MainSourceCodeCustomizer;
import io.spring.initializr.generator.spring.code.MainSourceCodeProjectContributor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class EntityModelGenerationConfiguration {

    private final ScribeProjectDescription description;

    private final IndentingWriterFactory indentingWriterFactory;

    @Bean
    EntityModel entityModel() {
        return new EntityModel(this.description.getChangeSet().getEntities());
    }

    @Bean
    public EntityModelSourceCodeProjectContributor entityModelJavaSourceCodeProjectContributor(
            EntityModel entityModel
            ) {
        return new EntityModelSourceCodeProjectContributor(this.description, entityModel, JavaSourceCode::new,
                new JavaSourceCodeWriter(this.indentingWriterFactory));
    }

}
