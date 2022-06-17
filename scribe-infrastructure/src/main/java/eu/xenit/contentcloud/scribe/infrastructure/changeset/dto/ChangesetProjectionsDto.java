package eu.xenit.contentcloud.scribe.infrastructure.changeset.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ChangesetProjectionsDto {

    private JsonNode base;

}
