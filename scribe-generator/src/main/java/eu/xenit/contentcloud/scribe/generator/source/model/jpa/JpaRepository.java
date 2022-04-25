package eu.xenit.contentcloud.scribe.generator.source.model.jpa;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.source.model.SourceFile;
import eu.xenit.contentcloud.scribe.generator.source.model.TypeModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface JpaRepository extends TypeModel {

    String entityClassName();

    default String repositoryName() {
        return this.className();
    }

    JpaRepository repositoryName(String name);

    static JpaRepositoryBuilder forEntity(String entityClassName) {
        return new JpaRepositoryBuilder(entityClassName);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class JpaRepositoryBuilder {

        @NonNull
        private String entityName;

        public JpaRepository withGenerator(TypeModelSourceCodeGenerator<JpaRepository> generator) {
            return new JpaRepositoryImpl(generator, entityName);
        }
    }
}


@Accessors(fluent = true, chain = true)
class JpaRepositoryImpl implements JpaRepository {

    private final TypeModelSourceCodeGenerator<JpaRepository> generator;

    @NonNull
    @Getter
    @Setter
    private String entityClassName;

    private String className;

    public JpaRepositoryImpl(@NonNull TypeModelSourceCodeGenerator<JpaRepository> generator,
            @NonNull String entityClassName) {
        this.generator = generator;
        this.entityClassName = entityClassName;
    }

    @Override
    public String className() {
        return className != null ? className : this.entityClassName + "Repository";
    }

    @Override
    public JpaRepository repositoryName(String name) {
        this.className = name;
        return this;
    }

    @Override
    public SourceFile generate() {
        return this.generator.createSourceFile(this);
    }


}