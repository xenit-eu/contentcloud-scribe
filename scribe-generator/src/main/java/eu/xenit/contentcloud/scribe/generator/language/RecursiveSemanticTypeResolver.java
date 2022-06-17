package eu.xenit.contentcloud.scribe.generator.language;

public interface RecursiveSemanticTypeResolver<T extends ResolvedTypeName> extends SemanticTypeResolver<T> {

    void initialize(SemanticTypeResolver<T> rootResolver);
}
