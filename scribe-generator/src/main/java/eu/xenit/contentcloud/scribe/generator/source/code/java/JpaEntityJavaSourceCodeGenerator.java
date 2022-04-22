package eu.xenit.contentcloud.scribe.generator.source.code.java;

import eu.xenit.contentcloud.bard.AnnotationSpec;
import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.FieldSpec;
import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.MethodSpec;
import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.bard.TypeSpec.Builder;
import eu.xenit.contentcloud.scribe.generator.service.PackageStructure;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.JpaEntityField;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.JpaEntityIdField;
import eu.xenit.contentcloud.scribe.generator.source.model.jpa.TypeModelSourceCodeGenerator;
import io.spring.initializr.generator.language.java.JavaLanguage;
import java.beans.Introspector;
import javax.lang.model.element.Modifier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class JpaEntityJavaSourceCodeGenerator extends JavaSourceCodeGenerator implements TypeModelSourceCodeGenerator<JpaEntity> {

    @NonNull
    private final JavaLanguage language;

    @NonNull
    protected final PackageStructure packageStructure;

    @Override
    public JavaSourceFile createSourceFile(JpaEntity jpaEntity) {
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

        return new JavaSourceFile(java);
    }

    void addConstructor(JpaEntity jpaEntity, Builder type) {
        if (jpaEntity.lombok().useNoArgsConstructor()) {
            type.addAnnotation(ClassName.get("lombok", "NoArgsConstructor"));
        } else {
            type.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
        }
    }

    void addProperty(JpaEntity jpaEntity,
            Builder type, JpaEntityField field) {
        var fieldSpec = FieldSpec.builder(
                field.type(),
                Introspector.decapitalize(field.name()),
                Modifier.PRIVATE);

        this.addGetter(jpaEntity, field, type, fieldSpec);
        this.addSetter(jpaEntity, field, type, fieldSpec);

        type.addField(fieldSpec.build());
    }

    void addSetter(JpaEntity jpaEntity, JpaEntityField field, Builder type, FieldSpec.Builder fieldSpec) {
        if (jpaEntity.lombok().useSetter()) {
            // there is a @Setter class annotation
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

    void addGetter(JpaEntity jpaEntity, JpaEntityField field, Builder type, FieldSpec.Builder fieldSpec) {
        // using lombok ?
        boolean useLombok = false;
        if (jpaEntity.lombok().useGetter()) {
            // there is a @Getter class annotation
        } else {
            MethodSpec getter = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return this.$L", fieldSpec.build().name)
                    .returns(fieldSpec.build().type)
                    .build();
            type.addMethod(getter);
        }
    }

    void addIdProperty(JpaEntity jpaEntity, Builder type) {
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

        this.addGetter(jpaEntity, id, type, fieldSpec);
        this.addSetter(jpaEntity, id, type, fieldSpec);

        type.addField(fieldSpec.build());
    }
}
