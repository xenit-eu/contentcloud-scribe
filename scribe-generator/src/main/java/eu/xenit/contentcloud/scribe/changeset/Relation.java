package eu.xenit.contentcloud.scribe.changeset;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
public class Relation {

    String name;
    String source;
    String target;

    boolean manySourcePerTarget;
    boolean manyTargetPerSource;

    boolean required;

}
