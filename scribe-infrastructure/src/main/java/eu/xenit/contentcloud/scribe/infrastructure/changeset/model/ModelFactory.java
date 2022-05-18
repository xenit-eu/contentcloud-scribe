package eu.xenit.contentcloud.scribe.infrastructure.changeset.model;

import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ChangesetDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface ModelFactory {

    Model createBaseModel(ChangesetDto changesetDto, MediaType contentType);

    default Model createBaseModel(ResponseEntity<EntityModel<ChangesetDto>> changesetResponse) {
        return createBaseModel(changesetResponse.getBody().getContent(), changesetResponse.getHeaders().getContentType());
    }
}
