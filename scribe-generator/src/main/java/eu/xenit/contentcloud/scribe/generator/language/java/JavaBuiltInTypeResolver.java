package eu.xenit.contentcloud.scribe.generator.language.java;

import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.source.types.BuiltInType;
import eu.xenit.contentcloud.scribe.generator.source.types.CollectionType;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;

public class JavaBuiltInTypeResolver implements SemanticTypeResolver<JavaTypeName> {

    @Override
    public JavaTypeName resolve(SemanticType type) {
        if (type instanceof BuiltInType) {
            return this.resolveBuiltInType((BuiltInType) type);
        }

        throw new TypeResolutionException(type);
    }

    @Override
    public boolean supports(SemanticType type) {
        return type instanceof BuiltInType;
    }

    JavaTypeName resolveBuiltInType(BuiltInType type) {
        if (BuiltInType.STRING.equals(type)) {
            return JavaTypeName.STRING;
        }

        if (BuiltInType.NUMBER.equals(type)) {
            return JavaTypeName.LONG;
        }

        if (BuiltInType.BOOLEAN.equals(type)) {
            return JavaTypeName.BOOLEAN;
        }

        if (BuiltInType.UUID.equals(type)) {
            return JavaTypeName.UUID;
        }

        if (BuiltInType.TIMESTAMP.equals(type)) {
            return JavaTypeName.INSTANT;
        }

        throw new TypeResolutionException(type);
    }
}
