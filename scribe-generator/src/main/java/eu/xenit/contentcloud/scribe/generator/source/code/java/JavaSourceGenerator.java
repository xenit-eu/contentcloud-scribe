package eu.xenit.contentcloud.scribe.generator.source.code.java;

import eu.xenit.contentcloud.scribe.generator.service.PackageStructure;
import eu.xenit.contentcloud.scribe.generator.source.SourceGenerator;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.JpaRepository;
import io.spring.initializr.generator.language.java.JavaLanguage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JavaSourceGenerator implements SourceGenerator {

    @NonNull
    private final JavaLanguage language;

    @NonNull
    private final PackageStructure packageStructure;

    @Override
    public JpaEntity createJpaEntity(String name) {
        return JpaEntity.withClassName(name)
                .withGenerator(new JpaEntityJavaSourceCodeGenerator(language, packageStructure));

    }

    @Override
    public JpaRepository createJpaRepository(String entityClassName) {
        return JpaRepository.forEntity(entityClassName)
                .withGenerator(new JpaRepositoryJavaSourceCodeGenerator(packageStructure));
    }

}
