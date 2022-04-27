package eu.xenit.contentcloud.scribe.generator.data.source.java;

import eu.xenit.contentcloud.scribe.generator.data.model.PackageStructure;
import eu.xenit.contentcloud.scribe.generator.data.source.SourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.data.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.data.model.jpa.JpaRepository;
import io.spring.initializr.generator.language.java.JavaLanguage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JavaSourceCodeGenerator implements SourceCodeGenerator {
    @NonNull
    private final JavaLanguage language;

    @NonNull
    private final PackageStructure packages;

    @Override
    public SourceFile createSourceFile(JpaEntity model) {
        return new JpaEntityJavaSourceCodeGenerator(packages).createSourceFile(model);
    }

    @Override
    public SourceFile createSourceFile(JpaRepository model) {
        return new JpaRepositoryJavaSourceCodeGenerator(packages).createSourceFile(model);
    }
}
