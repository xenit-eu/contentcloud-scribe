package eu.xenit.contentcloud.scribe.infrastructure.changeset.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ChangesetDto;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
public class ModelFactory {
    private final RestTemplate restTemplate;

    private ObjectMapper getObjectMapperFor(MediaType mediaType) {
        return restTemplate.getMessageConverters()
                .stream()
                .filter(AbstractJackson2HttpMessageConverter.class::isInstance)
                .map(AbstractJackson2HttpMessageConverter.class::cast)
                .map(converter -> converter.getObjectMappersForType(RepresentationModel.class).entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().includes(mediaType))
                        .map(Entry::getValue)
                        .findFirst()
                        .orElse(converter.getObjectMapper()))
                .findFirst()
                .orElseThrow();
    }

    public Model createBaseModel(ResponseEntity<EntityModel<ChangesetDto>> changesetResponse) {
        var baseModel = changesetResponse.getBody().getContent().getBaseModel();
        return new Model(getObjectMapperFor(changesetResponse.getHeaders().getContentType()), baseModel);
    }

}
