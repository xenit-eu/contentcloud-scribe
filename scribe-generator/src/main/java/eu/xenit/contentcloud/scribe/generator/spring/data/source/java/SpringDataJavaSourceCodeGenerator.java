package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaBuiltInTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SpringDataSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaRepository;
import io.spring.initializr.generator.language.java.JavaLanguage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringDataJavaSourceCodeGenerator implements SpringDataSourceCodeGenerator {

    @NonNull
    private final JavaLanguage language;

    @NonNull
    private final SpringDataPackageStructure packages;

    @NonNull
    private final SemanticTypeResolver<JavaTypeName> typeResolver;

    @Override
    public SourceFile createSourceFile(JpaEntity model) {
        return new JpaEntityJavaSourceCodeGenerator(packages, typeResolver).createSourceFile(model);
    }

    @Override
    public SourceFile createSourceFile(JpaRepository model) {
        return new JpaRepositoryJavaSourceCodeGenerator(packages).createSourceFile(model);
    }
}
