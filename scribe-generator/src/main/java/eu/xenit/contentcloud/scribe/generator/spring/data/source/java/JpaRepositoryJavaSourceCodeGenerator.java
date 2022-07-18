package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import eu.xenit.contentcloud.bard.AnnotationSpec;
import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.ParameterizedTypeName;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaRepository;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaRepositorySourceCodeGenerator;
import java.util.Objects;
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

        typeBuilder.addAnnotation(createRestRepositoryResourceAnnotation(repository));

        // customize repositories here
        var java = JavaFile.builder(packages.getRepositoriesPackageName(), typeBuilder.build())
                .indent("\t")
                .build();

        return java::writeToPath;
    }

    private AnnotationSpec createRestRepositoryResourceAnnotation(JpaRepository repository) {
        var repositoryRestResourceAnnotation = AnnotationSpec.builder(ClassName.get("org.springframework.data.rest.core.annotation", "RepositoryRestResource"));

        if(repository.defaultRestResource().exported() != repository.restResource().exported()) {
            repositoryRestResourceAnnotation.addMember("exported", "$L", repository.restResource().exported());
        }
        if(!Objects.equals(repository.defaultRestResource().getPathSegment(), repository.restResource().getPathSegment())) {
            repositoryRestResourceAnnotation.addMember("path", "$S", repository.restResource().getPathSegment());
        }

        if(!Objects.equals(repository.defaultRestResource().getCollectionResource().getRelationName(), repository.restResource().getCollectionResource().getRelationName())) {
            repositoryRestResourceAnnotation.addMember("collectionResourceRel", "$S", repository.restResource().getCollectionResource().getRelationName());
        }

        if(!Objects.equals(repository.defaultRestResource().getItemResource().getRelationName(), repository.restResource().getItemResource().getRelationName())) {
            repositoryRestResourceAnnotation.addMember("itemResourceRel", "$S", repository.restResource().getItemResource().getRelationName());
        }

        // TODO: Map the resource descriptions as well

        return repositoryRestResourceAnnotation.build();
    }
}
