package eu.xenit.contentcloud.scribe.generator.source.types;

import java.util.Optional;

public interface DataTypeResolver {

    /**
     * Resolves a data type identifier, to an {@link Optional<SemanticType>} or an empty {@link Optional} if
     * the data type could not be resolved.
     *
     * @param type is the datatype identifier
     * @return an optional with the resolved semantic type
     */
    Optional<SemanticType> resolve(String type);

}
