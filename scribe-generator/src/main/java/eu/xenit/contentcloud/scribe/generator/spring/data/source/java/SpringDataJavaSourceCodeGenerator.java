package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

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

    @Override
    public SourceFile createSourceFile(JpaEntity model) {
        return new JpaEntityJavaSourceCodeGenerator(packages).createSourceFile(model);
    }

    @Override
    public SourceFile createSourceFile(JpaRepository model) {
        return new JpaRepositoryJavaSourceCodeGenerator(packages).createSourceFile(model);
    }
}
