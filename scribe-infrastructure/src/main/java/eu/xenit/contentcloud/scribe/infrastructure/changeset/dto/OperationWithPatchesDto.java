package eu.xenit.contentcloud.scribe.infrastructure.changeset.dto;

import com.github.fge.jsonpatch.JsonPatch;
import java.util.Map;
import lombok.Data;

@Data
public class OperationWithPatchesDto {
    private String type;
    private Map<String,Object> properties;
    private JsonPatch patches;
}
