package eu.xenit.contentcloud.scribe.infrastructure.changeset.dto;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Model;
import eu.xenit.contentcloud.scribe.changeset.Policy;
import java.util.List;
import lombok.Data;

@Data
public class ModelDto {
    private List<Entity> entities;
    private List<Policy> policies;

    public Model toModel() {
        return new Model(entities, policies);
    }
}
