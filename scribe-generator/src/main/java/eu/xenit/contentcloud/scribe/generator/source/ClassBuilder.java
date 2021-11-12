package eu.xenit.contentcloud.scribe.generator.source;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
abstract class ClassBuilder<SELF extends TypeBuilder> implements TypeBuilder {

    @Getter(AccessLevel.PACKAGE)
    private final SourceGeneratorVisitor generator;

    @Getter
    private final String className;

    protected SELF self() {
        return (SELF) this;
    }




}
