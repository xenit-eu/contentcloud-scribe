package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.spring.data.model.TypeDeclaration;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface JpaRepository extends TypeDeclaration {

    String entityClassName();

    RestResourceEntity defaultRestResource();
    RestResourceEntity restResource();

    static JpaRepository forEntity(JpaEntity entity) {
        return new JpaRepositoryImpl(entity.className(), entity.defaultRestResource(), entity.restResource());
    }
}


@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
class JpaRepositoryImpl implements JpaRepository {

    @NonNull
    @Getter
    @Setter
    private String entityClassName;

    @NonNull
    @Getter
    private RestResourceEntity defaultRestResource;

    @NonNull
    @Getter
    private RestResourceEntity restResource;

    @Override
    public String className() {
        return this.entityClassName + "Repository";
    }

}