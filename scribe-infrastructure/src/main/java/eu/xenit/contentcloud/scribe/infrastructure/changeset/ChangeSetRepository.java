package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import eu.xenit.contentcloud.scribe.changeset.ChangeSetResolver;
import eu.xenit.contentcloud.scribe.changeset.ChangeSet;
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
public class ChangeSetRepository implements ChangeSetResolver {

    private final RestTemplate restTemplate;
    private final Set<PathPattern> allowedPaths;

    public ChangeSetRepository(ChangeSetRepositoryProperties properties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.allowedPaths = properties.getAllowedPaths();
    }

    @Override
    public ChangeSet get(URI changesetURI) {
        this.checkAllowedPaths(changesetURI);

        var changeset = this.restTemplate.exchange(
                        RequestEntity.get(changesetURI).accept(MediaTypes.HAL_FORMS_JSON).build(),
                        new ParameterizedTypeReference<EntityModel<ChangeSetModel>>() {})
                .getBody();

        var project = changeset.getLink("project")
                .map(Link::getHref)
                .map(URI::create)
                .map(projectURI -> RequestEntity.get(projectURI).accept(MediaTypes.HAL_FORMS_JSON).build())
                .map(projectRequest -> this.restTemplate.exchange(
                        projectRequest, ProjectModel.class))
                .map(HttpEntity::getBody)
                .orElseThrow();

        return new ChangeSet(
                project.getName(),
                project.getOrganization(),
                changeset.getContent().getEntities(),
                changeset.getContent().getOperations()
        );
    }

    private void checkAllowedPaths(URI changeSet) {
        var path = PathContainer.parsePath(changeSet.toString());
        if (this.allowedPaths.stream().noneMatch(pathPattern -> pathPattern.matches(path))) {
            log.warn("ChangeSet URI denied: " + changeSet);
            throw new IllegalArgumentException("URI is not allowed: " + changeSet);
        }
    }
}
