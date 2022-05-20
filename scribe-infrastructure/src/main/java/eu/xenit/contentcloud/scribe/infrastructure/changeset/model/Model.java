package eu.xenit.contentcloud.scribe.infrastructure.changeset.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ModelDto;
import lombok.Value;

@Value
public class Model {
    ObjectMapper objectMapper;
    JsonNode data;

    public Model patch(JsonPatch patch) throws JsonPatchException {
        return new Model(objectMapper, patch.apply(data));
    }

    public ModelDto toDto() throws JsonProcessingException {
        return objectMapper.treeToValue(data, ModelDto.class);
    }

}
