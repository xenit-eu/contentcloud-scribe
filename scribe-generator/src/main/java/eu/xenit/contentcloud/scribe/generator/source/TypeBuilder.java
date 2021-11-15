package eu.xenit.contentcloud.scribe.generator.source;

public interface TypeBuilder {

    String className();
    SourceFile generate();

}
