package eu.xenit.contentcloud.scribe.infrastructure.changeset.dto;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import java.util.List;
import lombok.Data;

@Data
public class ModelDto {
    private List<Entity> entities;
}
