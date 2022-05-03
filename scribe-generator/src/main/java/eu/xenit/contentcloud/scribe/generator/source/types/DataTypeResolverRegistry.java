package eu.xenit.contentcloud.scribe.generator.source.types;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DataTypeResolverRegistry implements DataTypeResolver {

    private final Collection<DataTypeResolver> resolvers;

    @Override
    public Optional<SemanticType> resolve(String dataType) {
        return this.resolvers.stream()
                .map(resolver -> resolver.resolve(dataType))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
