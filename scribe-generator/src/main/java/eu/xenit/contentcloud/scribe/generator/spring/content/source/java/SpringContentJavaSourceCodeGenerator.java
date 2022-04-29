package eu.xenit.contentcloud.scribe.generator.spring.content.source.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.ParameterizedTypeName;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.ContentStore;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.SpringContentPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.content.source.SpringContentSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringContentJavaSourceCodeGenerator implements SpringContentSourceCodeGenerator {

    @NonNull
    private final SpringDataPackageStructure springDataPackages;

    @NonNull
    private final SpringContentPackageStructure springContentPackages;

    @Override
    public SourceFile createSourceFile(ContentStore model) {

        var typeBuilder = TypeSpec.interfaceBuilder(model.className());
        typeBuilder.addSuperinterface(ParameterizedTypeName.get(
                ClassName.get("org.springframework.content.commons.repository", "ContentStore"),
                ClassName.get(this.springDataPackages.getModelPackageName(), model.entityClassName()),
                ClassName.get(String.class)
        ));

        model.exportAsRestResource(export -> {
            if (export.isEnabled()) {
                typeBuilder.addAnnotation(ClassName.get("org.springframework.content.rest", "StoreRestResource"));
            }
        });

        var java = JavaFile.builder(springContentPackages.getContentStorePackageName(), typeBuilder.build())
                .indent("\t")
                .build();

        return java::writeToPath;
    }
}
