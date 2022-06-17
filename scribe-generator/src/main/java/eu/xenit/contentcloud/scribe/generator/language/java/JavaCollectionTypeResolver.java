package eu.xenit.contentcloud.scribe.generator.language.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.ParameterizedTypeName;
import eu.xenit.contentcloud.scribe.generator.language.RecursiveSemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.ResolvedTypeName;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.source.types.BuiltInType;
import eu.xenit.contentcloud.scribe.generator.source.types.CollectionType;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class JavaCollectionTypeResolver implements RecursiveSemanticTypeResolver<JavaTypeName> {

    private SemanticTypeResolver<JavaTypeName> elementResolver;

    @Override
    public JavaTypeName resolve(SemanticType type) {
        if (type instanceof CollectionType) {
            return this.resolveCollectionType((CollectionType) type);
        }

        throw new TypeResolutionException(type);
    }

    @Override
    public boolean supports(SemanticType type) {
        return type instanceof CollectionType;
    }

    JavaTypeName resolveCollectionType(CollectionType type) {
        // resolve element type
        var elementType = this.elementResolver.resolve(type.getElementType());

        // wrap it in a List<>
        return new JavaTypeName(ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                elementType.getTypeName()
        ));

    }

    @Override
    public void initialize(@NonNull SemanticTypeResolver<JavaTypeName> rootResolver) {
        this.elementResolver = rootResolver;
    }
}
