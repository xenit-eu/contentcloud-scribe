package eu.xenit.contentcloud.scribe.generator.opa.model;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.Value;
import org.springframework.util.StringUtils;

@Value
public class In implements RegoRule {
    String variable;
    List<String> items;

    @Override
    public String toRego() {
        return "%s == %s[_]".formatted(variable, toRegoArray(items));
    }

    private static String toRegoArray(List<String> items) {
        return items.stream()
                .map(StringUtils::quote)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
