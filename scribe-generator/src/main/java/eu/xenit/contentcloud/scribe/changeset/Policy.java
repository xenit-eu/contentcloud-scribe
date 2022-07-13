package eu.xenit.contentcloud.scribe.changeset;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class Policy {
    @NonNull String id;
    @NonNull String entity;
    boolean requiresAuthentication;
    @NonNull List<String> verbs;
    @NonNull List<PolicyCondition> conditions;

}
