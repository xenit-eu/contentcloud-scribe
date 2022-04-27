package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.spring.data.model.TypeModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface JpaRepository extends TypeModel {

    String entityClassName();

    default String repositoryName() {
        return this.className();
    }

    JpaRepository repositoryName(String name);

    static JpaRepository forEntity(String entityClassName) {
        return new JpaRepositoryImpl(entityClassName);
    }
}


@Accessors(fluent = true, chain = true)
class JpaRepositoryImpl implements JpaRepository {

    @NonNull
    @Getter
    @Setter
    private String entityClassName;

    private String className;

    public JpaRepositoryImpl(@NonNull String entityClassName) {
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

}