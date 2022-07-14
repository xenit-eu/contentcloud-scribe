package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.CollectionType;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.NonNull;
import org.atteo.evo.inflector.English;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.StringUtils;

public interface OneToManyRelation extends JpaEntityProperty {


}

class OneToManyWithJoinColumnRelationImpl extends JpaEntityFieldImpl implements OneToManyRelation {

    @NonNull
    private final Supplier<String> sourceEntityName;

    OneToManyWithJoinColumnRelationImpl(@NonNull Supplier<String> sourceEntityName, SemanticType targetEntityType, String name) {
        super(wrapInCollectionType(targetEntityType), name);

        this.sourceEntityName = sourceEntityName;

        // if the field has been renamed, add a `@RestResource` annotation
        if (!Objects.equals(name, this.naming.fieldName())) {
            this.addAnnotation(Annotation.withType(SpringDataRestAnnotations.RestResource)
                    .withMembers(members -> {
                        members.put("rel", name);
                        members.put("path", English.plural(StringUtils.uncapitalize(name)));
                    }));
        }
    }

    private static SemanticType wrapInCollectionType(SemanticType targetEntityType) {
        return CollectionType.listOf(targetEntityType);
    }

    @Override
    public @NonNull SemanticType type() {
        return super.type();
    }

    @Override
    public Stream<Annotation> annotations() {
        return Stream.concat(
            Stream.of(
                    Annotation.withType(JpaAnnotations.OneToMany),
                    Annotation.withType(JpaAnnotations.JoinColumn)
                            .withMembers(members -> {
                                var joinColumnName = joinColumnName(sourceEntityName.get(), this.normalizedName());
                                members.put("name", joinColumnName);
                            })
            ),
            super.annotations()
        );
    }

    private static String joinColumnName(String sourceEntity, String relationName) {
        return "_%s_id__%s".formatted(
                ParsingUtils.reconcatenateCamelCase(sourceEntity, "_"),
                ParsingUtils.reconcatenateCamelCase(relationName, "_")
        );
    }
}
