package eu.xenit.contentcloud.scribe.generator.language.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.language.ResolvedTypeName;
import java.time.Instant;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JavaTypeName implements ResolvedTypeName {

    public static final JavaTypeName STRING = new JavaTypeName(ClassName.get(String.class));
    public static final JavaTypeName LONG = new JavaTypeName(TypeName.LONG);
    public static final JavaTypeName BOOLEAN = new JavaTypeName(TypeName.BOOLEAN);
    public static final JavaTypeName UUID = new JavaTypeName(ClassName.get(java.util.UUID.class));
    public static final JavaTypeName INSTANT = new JavaTypeName(ClassName.get(Instant.class));

    @Getter
    @NonNull
    private final TypeName typeName;

}
