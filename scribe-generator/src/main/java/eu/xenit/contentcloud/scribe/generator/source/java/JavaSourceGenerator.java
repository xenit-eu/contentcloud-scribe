package eu.xenit.contentcloud.scribe.generator.source.java;

import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.scribe.generator.repository.RepositoryPackageStructure;
import eu.xenit.contentcloud.scribe.generator.source.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.source.SourceGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JavaSourceGenerator implements SourceGenerator {

    @NonNull
    private final RepositoryPackageStructure packageStructure;

    @Override
    public JpaEntity createJpaEntity(String name) {
        return JpaEntity.withClassName(name)
                .withGenerator(jpaEntity -> generateJavaSource(new JpaEntityTypeSpec(jpaEntity).build())
        );
    }

    private JavaSourceFile generateJavaSource(TypeSpec typeSpec) {
        var java = JavaFile.builder(packageStructure.getModelPackageName(), typeSpec)
                .indent("\t")
                .build();

        return new JavaSourceFile(java);
    }


}
