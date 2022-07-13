package eu.xenit.contentcloud.scribe.changeset;

import lombok.NonNull;
import lombok.Value;

@Value
public class PolicyCondition {
    @NonNull PolicyConditionExpression left;
    @NonNull PolicyConditionOperator oper;
    @NonNull PolicyConditionExpression right;
}
