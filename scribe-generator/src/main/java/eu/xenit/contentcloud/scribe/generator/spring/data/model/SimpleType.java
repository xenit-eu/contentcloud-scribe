package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import java.lang.reflect.Type;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleType implements Type {

    @NonNull
    private final String packageName;

    @NonNull
    private final String typeName;


    public static Type get(String packageName, String typeName) {
        return new SimpleType(packageName, typeName);
    }

    @Override
    public String getTypeName() {
        return packageName + "." + typeName;
    }

}
