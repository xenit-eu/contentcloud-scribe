package eu.xenit.contentcloud.scribe.generator.language;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;

public interface SemanticTypeResolver<T extends ResolvedTypeName> {

    boolean supports(SemanticType type);

    T resolve(SemanticType type) throws TypeResolutionException;

    class TypeResolutionException extends RuntimeException {
        public TypeResolutionException(SemanticType type) {
            super("Cannot resolve '"+type+"'");
        }
    }

}


