package eu.xenit.contentcloud.scribe.changeset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class ChangeSet {

    String project;
    String organization;

    private List<Entity> entities;
    private List<Operation> operations;
}
