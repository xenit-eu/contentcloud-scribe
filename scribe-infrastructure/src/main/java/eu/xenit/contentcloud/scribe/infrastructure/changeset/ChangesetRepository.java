package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.ChangesetResolver;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ChangesetDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ProjectDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.model.RestTemplateModelFactory;
import java.net.URI;
import java.util.Set;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.server.PathContainer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.pattern.PathPattern;

@Slf4j
public class ChangesetRepository implements ChangesetResolver {

    private final RestTemplate restTemplate;
    private final Set<PathPattern> allowedPaths;
    private final ChangesetFactory changesetFactory;

    public ChangesetRepository(ChangesetRepositoryProperties properties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.allowedPaths = properties.getAllowedPaths();
        this.changesetFactory = new ChangesetFactory(new RestTemplateModelFactory(restTemplate));
    }

    @Override
    public Changeset get(URI changesetURI) {
        this.checkAllowedPaths(changesetURI);

        var changesetResponse = this.restTemplate.exchange(
                        RequestEntity.get(changesetURI).accept(MediaTypes.HAL_FORMS_JSON).build(),
                        new ParameterizedTypeReference<EntityModel<ChangesetDto>>() {
                        });
        var changeset = changesetResponse.getBody();

        var project = changeset.getLink("project")
                .map(Link::getHref)
                .map(URI::create)
                .map(projectURI -> RequestEntity.get(projectURI).accept(MediaTypes.HAL_FORMS_JSON).build())
                .map(projectRequest -> this.restTemplate.exchange(
                        projectRequest, ProjectDto.class))
                .map(HttpEntity::getBody)
                .orElseThrow();

        var parentLoader = changeset.getLink("parent")
                .map(Link::getHref)
                .map(URI::create)
                .map(uri -> ((Supplier<Changeset>)() -> this.get(uri)))
                .orElse(null);

        return changesetFactory.create(
                changeset.getContent(),
                project,
                changesetResponse.getHeaders().getContentType(),
                parentLoader
        );
    }

    private void checkAllowedPaths(URI changeset) {
        var path = PathContainer.parsePath(changeset.toString());
        if (this.allowedPaths.stream().noneMatch(pathPattern -> pathPattern.matches(path))) {
            log.warn("Changeset URI denied: " + changeset);
            throw new IllegalArgumentException("URI is not allowed: " + changeset);
        }
    }
}
