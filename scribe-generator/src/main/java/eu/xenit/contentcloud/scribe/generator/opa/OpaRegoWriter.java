package eu.xenit.contentcloud.scribe.generator.opa;

import eu.xenit.contentcloud.scribe.changeset.Policy;
import eu.xenit.contentcloud.scribe.changeset.PolicyCondition;
import eu.xenit.contentcloud.scribe.changeset.PolicyConditionExpression;
import eu.xenit.contentcloud.scribe.changeset.PolicyConditionExpressionType;
import eu.xenit.contentcloud.scribe.changeset.PolicyConditionOperator;
import eu.xenit.contentcloud.scribe.generator.opa.model.In;
import io.spring.initializr.generator.io.IndentingWriter;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class OpaRegoWriter {
    private static final Map<PolicyConditionExpressionType, Function<List<String>, String>> EXPRESSION_TYPES = Map.of(
            PolicyConditionExpressionType.AUTHENTICATION, (sub) -> ("input.auth."+String.join(".", sub)),
            PolicyConditionExpressionType.USER, sub -> "input.user."+String.join(".", sub),
            PolicyConditionExpressionType.ENTITY, sub -> "input.entity."+String.join(".", sub),
            PolicyConditionExpressionType.CONSTANT_BOOLEAN, sub -> Boolean.toString(Boolean.parseBoolean(sub.get(0))),
            PolicyConditionExpressionType.CONSTANT_NUMBER, sub -> Float.toString(Float.parseFloat(sub.get(0))),
            PolicyConditionExpressionType.CONSTANT_STRING, sub -> StringUtils.quote(sub.get(0))
    );

    @RequiredArgsConstructor
    private static class SimpleOperator implements BiFunction<String, String, String> {
        private final String operator;


        @Override
        public String apply(String a, String b) {
            return String.join(" ", a, operator, b);
        }
    }

    private static final Map<PolicyConditionOperator, BiFunction<String, String, String>> OPERATORS = Map.of(
            PolicyConditionOperator.EQUALS, new SimpleOperator("=="),
            PolicyConditionOperator.NOT_EQUALS, new SimpleOperator("!="),
            PolicyConditionOperator.GREATER_THAN, new SimpleOperator(">"),
            PolicyConditionOperator.GREATER_THAN_OR_EQUALS, new SimpleOperator(">="),
            PolicyConditionOperator.LESS_THAN, new SimpleOperator("<"),
            PolicyConditionOperator.LESS_THAN_OR_EQUALS, new SimpleOperator("<="),
            PolicyConditionOperator.IN, (a, b) -> a+ " == "+b+"[_]",
            PolicyConditionOperator.CONTAINS, (a, b) -> a + "[_] == "+b
    );

    private static final Map<String, String> VERBS_TO_HTTP = Map.of(
            "read", "GET",
            "update", "PUT",
            "create", "POST",
            "delete", "DELETE"
    );

    private final IndentingWriter writer;

    public void writePolicy(Policy policy) {
        writer.println("# Policy %s".formatted(policy.getId()));
        writer.println("allow {");
        writer.indented(() -> {
            writer.println(new In(
                    "input.method", policy.getVerbs().stream().map(VERBS_TO_HTTP::get).collect(Collectors.toList())
            ).toRego());
            // TODO: get path for an entity
            writer.println("# TODO: Path for entity %s".formatted(policy.getEntity()));
            for (PolicyCondition condition : policy.getConditions()) {
                writeCondition(condition);
            }

        });

        writer.println("}");
    }

    private void writeCondition(PolicyCondition condition) {
        var left = getConditionExpression(condition.getLeft());
        var right = getConditionExpression(condition.getRight());

        writer.println(OPERATORS.get(condition.getOper()).apply(left, right));
    }

    private static String getConditionExpression(PolicyConditionExpression expression) {
        return (EXPRESSION_TYPES.get(expression.getType()).apply(expression.getValue()));
    }

}
