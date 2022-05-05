package eu.xenit.contentcloud.scribe.generator.source.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents a basic type, which has a representation in the JVM standard libraries of the JVM-languages.
 *
 * Constructor is package private, instances are created in {@link SemanticType}
 */
@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BuiltInType implements SemanticType {

    private final String keyword;
}
