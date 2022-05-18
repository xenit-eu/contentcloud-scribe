package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.ChangesetResolver;
import eu.xenit.contentcloud.scribe.changeset.Model;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ChangesetDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.OperationWithPatchesDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ProjectDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.model.ModelFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
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
    private final ModelFactory modelFactory;

    public ChangesetRepository(ChangesetRepositoryProperties properties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.allowedPaths = properties.getAllowedPaths();
        this.modelFactory = new ModelFactory(restTemplate);
    }

    @SneakyThrows
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

        var baseModel = modelFactory.createBaseModel(changesetResponse);
        var currentModel = baseModel;

        List<Operation> operations = new ArrayList<>(changeset.getContent().getOperations().size());
        for(OperationWithPatchesDto operation: changeset.getContent().getOperations()) {
            var beforeModel = currentModel;
            currentModel = currentModel.patch(operation.getPatches());
            var afterModel = currentModel;
            operations.add(new Operation(
                    operation.getType(),
                    operation.getProperties(),
                    new Model(beforeModel.toDto().getEntities()),
                    new Model(afterModel.toDto().getEntities())
            ));
        }


        return Changeset.builder()
                .baseModel(new Model(baseModel.toDto().getEntities()))
                .project(project.getName())
                .organization(project.getOrganization())
                .entities(changeset.getContent().getEntities())
                .operations(operations)
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
