package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import static java.lang.Character.isISOControl;

import eu.xenit.contentcloud.bard.AnnotationSpec;
import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.FieldSpec;
import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.MethodSpec;
import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.bard.TypeSpec.Builder;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JacksonAnnotations;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntitySourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.SpringDataRestAnnotations;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntityComponent;
import java.util.Objects;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class JpaEntityJavaSourceCodeGenerator implements JpaEntitySourceCodeGenerator {

    @NonNull
    protected final SpringDataPackageStructure packageStructure;

    protected final SemanticTypeResolver<JavaTypeName> typeResolver;

    @Override
    public SourceFile createSourceFile(JpaEntity jpaEntity) {
        var type = TypeSpec.classBuilder(jpaEntity.className())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("javax.persistence", "Entity"));

        this.addConstructor(jpaEntity, type);

        if (jpaEntity.lombok().useGetter()) {
            type.addAnnotation(ClassName.get("lombok", "Getter"));
        }

        if (jpaEntity.lombok().useSetter()) {
            type.addAnnotation(ClassName.get("lombok", "Setter"));
        }

        this.addIdProperty(jpaEntity, type);
        jpaEntity.fields().forEachOrdered(field -> this.addProperty(jpaEntity, type, field));

        var java = JavaFile.builder(packageStructure.getModelPackageName(), type.build())
                .indent("\t")
                .build();

        return java::writeToPath;
    }

    void addConstructor(JpaEntity jpaEntity, Builder type) {
        if (jpaEntity.lombok().useNoArgsConstructor()) {
            type.addAnnotation(ClassName.get("lombok", "NoArgsConstructor"));
        } else {
            type.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
        }
    }

    void addProperty(JpaEntity jpaEntity, Builder type, JavaBeanProperty field) {

        var fieldSpec = FieldSpec.builder(
                this.typeResolver.resolve(field.type()).getTypeName(),
                field.fieldName(),
                Modifier.PRIVATE);

        var maybeRestField = jpaEntity.restResource().findComponent(field);
        var maybeDefaultRestField = jpaEntity.defaultRestResource().findComponent(field);

        this.addGetter(jpaEntity, type, fieldSpec);
        this.addSetter(jpaEntity, type, fieldSpec);

        field.annotations().forEach(annotationType -> {
            var annotationClassName = typeResolver.resolve(annotationType.getType());
            var annotation = AnnotationSpec.builder((ClassName) annotationClassName.getTypeName());

            annotationType.withMembers(members -> {
                members.forEach((name, value) -> {
                    addAnnotationMember_makePublicInBard(annotation, name, value);
                });
            });

            fieldSpec.addAnnotation(annotation.build());
        });

        maybeRestField.flatMap(RestResourceEntityComponent::asAttribute).ifPresent(restResourceAttribute -> {
            maybeDefaultRestField.flatMap(RestResourceEntityComponent::asAttribute).ifPresent(defaultRestResourceAttribute -> {
                if(!Objects.equals(restResourceAttribute.getRestAttributeName(), defaultRestResourceAttribute.getRestAttributeName())) {
                    var jsonPropertyTypeName = typeResolver.resolve(JacksonAnnotations.JsonProperty).getTypeName();
                    var jsonPropertyAnnotation = AnnotationSpec.builder((ClassName) jsonPropertyTypeName);
                    jsonPropertyAnnotation.addMember("value", "$S", restResourceAttribute.getRestAttributeName());
                    fieldSpec.addAnnotation(jsonPropertyAnnotation.build());
                }
                if(!Objects.equals(restResourceAttribute.isExported(), defaultRestResourceAttribute.isExported())) {
                    var jsonIgnoreTypeName = typeResolver.resolve(JacksonAnnotations.JsonIgnore).getTypeName();
                    var jsonIgnoreAnnotation = AnnotationSpec.builder((ClassName) jsonIgnoreTypeName);
                    fieldSpec.addAnnotation(jsonIgnoreAnnotation.build());
                }
            });
        });

        maybeRestField.flatMap(RestResourceEntityComponent::asRelation).ifPresent(restResourceRelation -> {
            maybeDefaultRestField.flatMap(RestResourceEntityComponent::asRelation).ifPresent(defaultRestResourceRelation -> {
                var restResourceTypeName = typeResolver.resolve(SpringDataRestAnnotations.RestResource).getTypeName();
                var restResourceAnnotationBuilder = AnnotationSpec.builder((ClassName) restResourceTypeName);
                if(!Objects.equals(restResourceRelation.getRestRelationName(), defaultRestResourceRelation.getRestRelationName())) {
                    restResourceAnnotationBuilder.addMember("rel", "$S", restResourceRelation.getRestRelationName());
                }
                if(!Objects.equals(restResourceRelation.getPathSegment(), defaultRestResourceRelation.getPathSegment())) {
                    restResourceAnnotationBuilder.addMember("path", "$S", restResourceRelation.getPathSegment());
                }
                if(!Objects.equals(restResourceRelation.isExported(), defaultRestResourceRelation.isExported())) {
                    restResourceAnnotationBuilder.addMember("exported", "$L", restResourceRelation.isExported());
                }

                var restResourceAnnotation = restResourceAnnotationBuilder.build();

                if(!restResourceAnnotation.members.isEmpty()) {
                    fieldSpec.addAnnotation(restResourceAnnotation);
                }
            });
        });

        type.addField(fieldSpec.build());
    }

    void addSetter(JpaEntity jpaEntity, Builder type, FieldSpec.Builder fieldSpec) {
        if (jpaEntity.lombok().useSetter()) {
            // there is a @Setter class annotation
            return;
        }

        String fieldName = fieldSpec.build().name;

        MethodSpec setter = MethodSpec.methodBuilder("set" + StringUtils.capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldSpec.build().type, fieldName)
                .addStatement("this.$L = $L", fieldName, fieldName)
                .returns(TypeName.VOID)
                .build();
        type.addMethod(setter);

    }

    void addGetter(JpaEntity jpaEntity, Builder type, FieldSpec.Builder fieldSpec) {
        if (jpaEntity.lombok().useGetter()) {
            // there is a @Getter class annotation
            return;
        }

        var fieldName = fieldSpec.build().name;
        MethodSpec getter = MethodSpec.methodBuilder("get" + StringUtils.capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$L", fieldName)
                .returns(fieldSpec.build().type)
                .build();
        type.addMethod(getter);
    }

    void addIdProperty(JpaEntity jpaEntity, Builder type) {
        var idField = jpaEntity.id();
        var idTypeName = this.typeResolver.resolve(idField.type()).getTypeName();
        var fieldSpec = FieldSpec.builder(idTypeName, idField.fieldName(), Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("javax.persistence", "Id")).build());

        //        switch (id.generationStrategy()) {
        //            case AUTO:
        fieldSpec.addAnnotation(AnnotationSpec.builder(ClassName.get("javax.persistence", "GeneratedValue"))
                .addMember("strategy", "$T.$L",
                        ClassName.get("javax.persistence", "GenerationType"), "AUTO")
                .build());
        //        }

        this.addGetter(jpaEntity, type, fieldSpec);
        this.addSetter(jpaEntity, type, fieldSpec);

        type.addField(fieldSpec.build());
    }


    private static AnnotationSpec.Builder addAnnotationMember_makePublicInBard(AnnotationSpec.Builder _this,
            String memberName, Object value) {

        if (value instanceof Class<?>) {
            return _this.addMember(memberName, "$T.class", value);
        }
        if (value instanceof Enum) {
            return _this.addMember(memberName, "$T.$L", value.getClass(), ((Enum<?>) value).name());
        }
        if (value instanceof String) {
            return _this.addMember(memberName, "$S", value);
        }
        if (value instanceof Float) {
            return _this.addMember(memberName, "$Lf", value);
        }
        if (value instanceof Character) {
            return _this.addMember(memberName, "'$L'", characterLiteralWithoutSingleQuotes((char) value));
        }
        return _this.addMember(memberName, "$L", value);

    }

    static String characterLiteralWithoutSingleQuotes(char c) {
        // see https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
        switch (c) {
            case '\b':
                return "\\b"; /* \u0008: backspace (BS) */
            case '\t':
                return "\\t"; /* \u0009: horizontal tab (HT) */
            case '\n':
                return "\\n"; /* \u000a: linefeed (LF) */
            case '\f':
                return "\\f"; /* \u000c: form feed (FF) */
            case '\r':
                return "\\r"; /* \u000d: carriage return (CR) */
            case '\"':
                return "\"";  /* \u0022: double quote (") */
            case '\'':
                return "\\'"; /* \u0027: single quote (') */
            case '\\':
                return "\\\\";  /* \u005c: backslash (\) */
            default:
                return isISOControl(c) ? String.format("\\u%04x", (int) c) : Character.toString(c);
        }
    }
}
