package eu.xenit.contentcloud.scribe.changeset;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Changeset {

    String project;
    String organization;

    @NonNull
    List<Entity> entities;

    @NonNull
    List<Operation> operations;
}
