package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SpringDataPackageStructure;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.EntityTypeName;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class JavaEntityTypeNameResolver implements SemanticTypeResolver<JavaTypeName> {

    private final SpringDataPackageStructure packageStructure;

    @Override
    public boolean supports(SemanticType type) {
        return type instanceof EntityTypeName;
    }

    @Override
    public JavaTypeName resolve(SemanticType type) throws TypeResolutionException {
        var entity = ((EntityTypeName) type).getEntity();

        String className = StringUtils.capitalize(entity.getName());
        if (hasInvalidChar(className) /* || check blacklist ? */) {
            throw new IllegalArgumentException("Entity name '" + className + "' contains invalid characters");
        }

        return new JavaTypeName(ClassName.get(packageStructure.getModelPackageName(), className));
    }

    private static boolean hasInvalidChar(String text) {
        if (!Character.isJavaIdentifierStart(text.charAt(0))) {
            return true;
        }
        if (text.length() > 1) {
            for (int i = 1; i < text.length(); i++) {
                if (!Character.isJavaIdentifierPart(text.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }


}
