package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ChangesetDto;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.EntityModel;

@JsonTest
class ChangesetDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void canParseWithJackson() throws IOException {

        assertThat(objectMapper).isNotNull();

        var changesetLink = new ClassPathResource("fixtures/changeset-sample.json").getURL();
        var typeReference = new TypeReference<EntityModel<ChangesetDto>>() {};
        var changeset = objectMapper.readValue(changesetLink, typeReference);

        assertThat(changeset).isNotNull();
    }

}