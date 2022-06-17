package eu.xenit.contentcloud.scribe.generator.language;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.Collection;

public class SemanticTypeResolverRegistry<T extends ResolvedTypeName> implements SemanticTypeResolver<T>{

    private final Collection<SemanticTypeResolver<T>> resolvers;

    public SemanticTypeResolverRegistry(Collection<SemanticTypeResolver<T>> resolvers) {
        this.resolvers = resolvers;

        this.resolvers.stream()
                .filter(RecursiveSemanticTypeResolver.class::isInstance)
                .map(res -> (RecursiveSemanticTypeResolver<T>) res)
                .forEach(resolver -> resolver.initialize(this));
    }

    @Override
    public T resolve(SemanticType type) throws TypeResolutionException {
        return this.resolvers.stream()
                .filter(resolver -> resolver.supports(type))
                .map(resolver -> resolver.resolve(type))
                .findFirst()
                .orElseThrow(() -> new TypeResolutionException(type));
    }

    @Override
    public boolean supports(SemanticType type) {
        return this.resolvers.stream().anyMatch(resolver -> resolver.supports(type));
    }
}
