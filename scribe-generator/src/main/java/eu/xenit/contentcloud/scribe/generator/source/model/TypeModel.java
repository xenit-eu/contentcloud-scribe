package eu.xenit.contentcloud.scribe.generator.source.model;

public interface TypeModel {

    String className();

    SourceFile generate();
}
