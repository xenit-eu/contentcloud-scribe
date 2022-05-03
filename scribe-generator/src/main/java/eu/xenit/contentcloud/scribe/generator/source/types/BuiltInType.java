package eu.xenit.contentcloud.scribe.generator.source.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BuiltInType implements SemanticType {

    private final String keyword;
}
