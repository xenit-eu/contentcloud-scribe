package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import eu.xenit.contentcloud.scribe.changeset.ChangesetResolver;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
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

import java.net.URI;
import java.util.Set;

@Slf4j
public class ChangesetRepository implements ChangesetResolver {

    private final RestTemplate restTemplate;
    private final Set<PathPattern> allowedPaths;

    public ChangesetRepository(ChangesetRepositoryProperties properties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.allowedPaths = properties.getAllowedPaths();
    }

    @Override
    public Changeset get(URI changesetURI) {
        this.checkAllowedPaths(changesetURI);

        var changeset = this.restTemplate.exchange(
                        RequestEntity.get(changesetURI).accept(MediaTypes.HAL_FORMS_JSON).build(),
                        new ParameterizedTypeReference<EntityModel<ChangesetModel>>() {})
                .getBody();

        var project = changeset.getLink("project")
                .map(Link::getHref)
                .map(URI::create)
                .map(projectURI -> RequestEntity.get(projectURI).accept(MediaTypes.HAL_FORMS_JSON).build())
                .map(projectRequest -> this.restTemplate.exchange(
                        projectRequest, ProjectModel.class))
                .map(HttpEntity::getBody)
                .orElseThrow();

        return Changeset.builder()
                .project(project.getName())
                .organization(project.getOrganization())
                .entities(changeset.getContent().getEntities())
                .operations(changeset.getContent().getOperations())
                .build();
    }

    private void checkAllowedPaths(URI changeset) {
        var path = PathContainer.parsePath(changeset.toString());
        if (this.allowedPaths.stream().noneMatch(pathPattern -> pathPattern.matches(path))) {
            log.warn("Changeset URI denied: " + changeset);
            throw new IllegalArgumentException("URI is not allowed: " + changeset);
        }
    }
}
