package eu.xenit.contentcloud.scribe.generator.language;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.Collection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SemanticTypeResolverRegistry<T extends ResolvedTypeName> implements SemanticTypeResolver<T>{

    private final Collection<SemanticTypeResolver<T>> resolvers;

    @Override
    public T resolve(SemanticType type) {
        return this.resolvers.stream()
                .filter(resolver -> resolver.supports(type))
                .map(resolver -> resolver.resolve(type))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public boolean supports(SemanticType type) {
        return this.resolvers.stream().anyMatch(resolver -> resolver.supports(type));
    }
}
