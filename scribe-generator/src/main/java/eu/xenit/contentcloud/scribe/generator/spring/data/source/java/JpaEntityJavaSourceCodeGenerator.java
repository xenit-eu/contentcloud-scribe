package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import eu.xenit.contentcloud.bard.AnnotationSpec;
import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.FieldSpec;
import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.bard.MethodSpec;
import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.bard.TypeSpec;
import eu.xenit.contentcloud.bard.TypeSpec.Builder;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaBuiltInTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntitySourceCodeGenerator;
import java.beans.Introspector;
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

    void addProperty(JpaEntity jpaEntity, Builder type, JpaEntityProperty field) {

        var fieldSpec = FieldSpec.builder(
                this.typeResolver.resolve(field.type()).getTypeName(),
                Introspector.decapitalize(field.name()),
                Modifier.PRIVATE);

        this.addGetter(jpaEntity, field, type, fieldSpec);
        this.addSetter(jpaEntity, field, type, fieldSpec);

        field.annotations().forEach(annotationType -> {
            fieldSpec.addAnnotation(ClassName.bestGuess(annotationType.getTypeName()));
        });

        type.addField(fieldSpec.build());
    }

    void addSetter(JpaEntity jpaEntity, JpaEntityProperty field, Builder type, FieldSpec.Builder fieldSpec) {
        if (jpaEntity.lombok().useSetter()) {
            // there is a @Setter class annotation
            return;
        }

        MethodSpec setter = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.name()))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldSpec.build().type, field.name())
                .addStatement("this.$L = $L", fieldSpec.build().name, field.name())
                .returns(TypeName.VOID)
                .build();
        type.addMethod(setter);

    }

    void addGetter(JpaEntity jpaEntity, JpaEntityProperty field, Builder type, FieldSpec.Builder fieldSpec) {
        if (jpaEntity.lombok().useGetter()) {
            // there is a @Getter class annotation
            return;
        }

        MethodSpec getter = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.name()))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$L", fieldSpec.build().name)
                .returns(fieldSpec.build().type)
                .build();
        type.addMethod(getter);
    }

    void addIdProperty(JpaEntity jpaEntity, Builder type) {
        var idField = jpaEntity.id();
        var idTypeName = this.typeResolver.resolve(idField.type()).getTypeName();
        var fieldSpec = FieldSpec.builder(idTypeName, idField.name(), Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("javax.persistence", "Id")).build());

        //        switch (id.generationStrategy()) {
        //            case AUTO:
        fieldSpec.addAnnotation(AnnotationSpec.builder(ClassName.get("javax.persistence", "GeneratedValue"))
                .addMember("strategy", "$T.$L",
                        ClassName.get("javax.persistence", "GenerationType"), "AUTO")
                .build());
        //        }

        this.addGetter(jpaEntity, idField, type, fieldSpec);
        this.addSetter(jpaEntity, idField, type, fieldSpec);

        type.addField(fieldSpec.build());
    }
}
