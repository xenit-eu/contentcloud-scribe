package io.spring.initializr.generator.language;

import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface TypeName {

    String getTypeName();

    static TypeName of(String name) {
        return new TypeNameImpl(name);
    }

    String getUnqualifiedName();

    boolean isParameterizedType();
    List<TypeName> getTypeParameters();

    TypeName getRawType();

    String getPackageName();
}

class TypeNameImpl implements TypeName {

    @Getter
    private final String typeName;

    TypeNameImpl(@NonNull String name) {
        this.typeName = name.trim();
    }

    @Override
    public String getUnqualifiedName() {
        return getUnqualifiedName(this.typeName);
    }

    @Override
    public boolean isParameterizedType() {
        return this.typeName.contains("<");
    }

    @Override
    public List<TypeName> getTypeParameters() {
        if (!this.isParameterizedType()) {
            return List.of();
        }

        return Arrays.stream(this.typeName.substring(this.typeName.indexOf('<') + 1, this.typeName.lastIndexOf('>'))
                .split(","))
                .map(TypeName::of)
                .collect(Collectors.toList());
    }

    @Override
    public TypeName getRawType() {
        if (!this.isParameterizedType()) {
            return this;
        }

        return TypeName.of(this.typeName.substring(0, this.typeName.indexOf('<')));
    }

    @Override
    public String getPackageName() {
        if (this.typeName.contains(".")) {
            return this.typeName.substring(0, typeName.lastIndexOf('.'));
        } else {
            return "";
        }
    }

    private static String getUnqualifiedName(String name) {
        if (!name.contains(".")) {
            return name;
        }

        if (name.contains("<")) {
            int openTypeParametersIndex = name.indexOf('<');
            return new StringBuilder()
                    .append(getUnqualifiedName(name.substring(0, openTypeParametersIndex)))
                    .append("<")
                    .append(Arrays.stream(name.substring(openTypeParametersIndex + 1, name.indexOf('>')).split(","))
                            .map(TypeNameImpl::getUnqualifiedName)
                            .collect(Collectors.joining(", ")))
                    .append(">")
                    .toString();
        }

        return name.substring(name.lastIndexOf(".") + 1);
    }

}
