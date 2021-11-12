package eu.xenit.contentcloud.scribe.generator.source.java;

import eu.xenit.contentcloud.bard.AnnotationSpec;
import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.FieldSpec;
import eu.xenit.contentcloud.bard.MethodSpec;
import eu.xenit.contentcloud.bard.ParameterSpec;
import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.scribe.generator.source.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.source.jpa.JpaEntityField;
import eu.xenit.contentcloud.scribe.generator.source.jpa.JpaEntityIdField;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.beans.Introspector;
import java.util.function.Consumer;

@RequiredArgsConstructor
class JpaEntityTypeSpec {

    @NonNull
    private final JpaEntity jpaEntity;

    public TypeSpec build() {
        var type = TypeSpec.classBuilder(jpaEntity.className())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(ClassName.get("javax.persistence", "Entity"));

        this.addConstructor(type);
        this.addIdProperty(type);
        jpaEntity.fields().forEachOrdered(field -> addProperty(type, field));

        return type.build();
    }

    private void addConstructor(TypeSpec.Builder type) {
        type.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
    }

    private void addProperty(TypeSpec.Builder type, JpaEntityField field) {
        var fieldSpec = FieldSpec.builder(
                field.type(),
                Introspector.decapitalize(field.name()),
                Modifier.PRIVATE);

        this.addGetter(field, type, fieldSpec);
        this.addSetter(field, type, fieldSpec);

        type.addField(fieldSpec.build());
    }

    private void addSetter(JpaEntityField field, TypeSpec.Builder type, FieldSpec.Builder fieldSpec) {
        boolean useLombok = false;
        if (useLombok) {
            // is there a @Getter on class-level ?
        } else {
            MethodSpec setter = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(fieldSpec.build().type, field.name())
                    .addStatement("this.$L = $L", fieldSpec.build().name, field.name())
                    .returns(TypeName.VOID)
                    .build();
            type.addMethod(setter);
        }
    }

    private void addGetter(JpaEntityField field, TypeSpec.Builder type, FieldSpec.Builder fieldSpec) {
        // using lombok ?
        boolean useLombok = false;
        if (useLombok) {
            // is there a @Getter on class-level ?
        } else {
            MethodSpec getter = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return this.$L", fieldSpec.build().name)
                    .returns(fieldSpec.build().type)
                    .build();
            type.addMethod(getter);
        }
    }

    private void addIdProperty(TypeSpec.Builder type) {
        JpaEntityIdField id = jpaEntity.id();
        var fieldSpec = FieldSpec.builder(id.type(), id.name(), Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("javax.persistence", "Id")).build());

//        switch (id.generationStrategy()) {
//            case AUTO:
        fieldSpec.addAnnotation(AnnotationSpec.builder(ClassName.get("javax.persistence", "GeneratedValue"))
            .addMember("strategy", "$T.$L",
                    ClassName.get("javax.persistence", "GenerationType"), "AUTO")
            .build());
//        }

        this.addGetter(id, type, fieldSpec);
        this.addSetter(id, type, fieldSpec);

        type.addField(fieldSpec.build());
    }


}
