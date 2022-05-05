package eu.xenit.contentcloud.scribe.generator.source.types;

public interface SemanticType {

    SemanticType STRING = new BuiltInType("STRING");
    SemanticType NUMBER = new BuiltInType("NUMBER");
    SemanticType UUID = new BuiltInType("UUID");
    SemanticType TIMESTAMP = new BuiltInType("TIMESTAMP");

}