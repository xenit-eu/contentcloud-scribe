package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.ParameterizedTypeName;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaRepository;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaRepositorySourceCodeGenerator;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class JpaRepositoryJavaSourceCodeGenerator implements JpaRepositorySourceCodeGenerator {

    @NonNull
    private final SpringDataPackageStructure packages;

    @Override
    public SourceFile createSourceFile(JpaRepository repository) {
        var typeBuilder = TypeSpec.interfaceBuilder(repository.className());
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

        return java::writeToPath;
    }
}
