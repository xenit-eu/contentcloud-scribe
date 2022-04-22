package eu.xenit.contentcloud.scribe.generator.source.code.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.ParameterizedTypeName;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.scribe.generator.service.PackageStructure;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.JpaRepository;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.TypeModelSourceCodeGenerator;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaRepositoryJavaSourceCodeGenerator implements TypeModelSourceCodeGenerator<JpaRepository> {

    @NonNull
    private final PackageStructure packages;

    @Override
    public JavaSourceFile createSourceFile(JpaRepository repository) {
        var typeBuilder = TypeSpec.interfaceBuilder(repository.repositoryName());
        typeBuilder.addSuperinterface(ParameterizedTypeName.get(
                ClassName.get("org.springframework.data.jpa.repository", "JpaRepository"),
                ClassName.get(this.packages.getModelPackageName(), repository.entityClassName()),
                ClassName.get(UUID.class)
        ));
        typeBuilder.addSuperinterface(ParameterizedTypeName.get(
                ClassName.get("org.springframework.data.querydsl", "QuerydslPredicateExecutor"),
                ClassName.get(packages.getModelPackageName(), repository.entityClassName())
        ));

        typeBuilder.addAnnotation(ClassName.get("org.springframework.data.rest.core.annotation", "RepositoryRestResource"));

        // customize repositories here
        var java = JavaFile.builder(packages.getRepositoriesPackageName(), typeBuilder.build())
                .indent("\t")
                .build();

        return new JavaSourceFile(java);
    }
}
