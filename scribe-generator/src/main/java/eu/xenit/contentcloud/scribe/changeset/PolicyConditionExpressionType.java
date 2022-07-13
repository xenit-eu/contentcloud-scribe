package eu.xenit.contentcloud.scribe.changeset;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PolicyConditionExpressionType {
    ENTITY("entity"),
    USER("user"),
    AUTHENTICATION("auth"),
    CONSTANT_NUMBER("constant:number"),
    CONSTANT_BOOLEAN("constant:boolean"),
    CONSTANT_STRING("constant:string");

    private final String serializedValue;

    @JsonCreator
    public static PolicyConditionExpressionType fromString(String serializedValue) {
        return Arrays.stream(PolicyConditionExpressionType.values())
                .filter(expr -> Objects.equals(expr.serializedValue, serializedValue))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown policy condition expression type '%s'".formatted(serializedValue)));
    }


}
