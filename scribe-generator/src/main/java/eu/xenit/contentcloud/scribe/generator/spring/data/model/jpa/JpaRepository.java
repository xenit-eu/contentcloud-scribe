package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.spring.data.model.TypeDeclaration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface JpaRepository extends TypeDeclaration {

    String entityClassName();

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

    public JpaRepositoryImpl(@NonNull String entityClassName) {
        this.entityClassName = entityClassName;
    }

    @Override
    public String className() {
        return this.entityClassName + "Repository";
    }

}