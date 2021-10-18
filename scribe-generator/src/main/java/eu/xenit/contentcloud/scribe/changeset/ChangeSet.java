package eu.xenit.contentcloud.scribe.changeset;

import lombok.Data;

import java.util.List;

@Data
public class ChangeSet {

    private List<Entity> entities;
    private List<Operation> operations;

}
