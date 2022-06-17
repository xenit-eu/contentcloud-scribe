package eu.xenit.contentcloud.scribe.infrastructure.changeset.dto;

import com.fasterxml.jackson.databind.JsonNode;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import java.util.List;
import lombok.Data;

@Data
public class ChangesetDto {
    private ChangesetProjectionsDto projections;

    private List<Entity> entities;
    private List<OperationWithPatchesDto> operations;

}
