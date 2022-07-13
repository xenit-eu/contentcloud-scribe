package eu.xenit.contentcloud.scribe.changeset;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PolicyConditionOperator {
    EQUALS("equals"),
    NOT_EQUALS("not_equals"),
    GREATER_THAN("greater_than"),
    GREATER_THAN_OR_EQUALS("greater_or_equals"),
    LESS_THAN("less_than"),
    LESS_THAN_OR_EQUALS("less_than_or_equals"),
    CONTAINS("contains"),
    IN("in");

    private final String serializedValue;

    @JsonCreator
    public static PolicyConditionOperator fromString(String serializedValue) {
        return Arrays.stream(PolicyConditionOperator.values())
                .filter(expr -> Objects.equals(expr.serializedValue, serializedValue))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown policy condition operator '%s'".formatted(serializedValue)));
    }
}
