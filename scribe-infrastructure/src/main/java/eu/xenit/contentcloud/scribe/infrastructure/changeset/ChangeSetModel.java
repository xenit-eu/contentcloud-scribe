package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import lombok.Data;

import java.util.List;

@Data
public class ChangeSetModel {

    private List<Entity> entities;
    private List<Operation> operations;

}
