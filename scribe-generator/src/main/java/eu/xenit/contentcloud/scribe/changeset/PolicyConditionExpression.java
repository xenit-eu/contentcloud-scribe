package eu.xenit.contentcloud.scribe.changeset;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class PolicyConditionExpression {
    @NonNull PolicyConditionExpressionType type;
    @NonNull List<String> value;


}
