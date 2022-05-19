package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.changeset.Model;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ChangesetDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.OperationWithPatchesDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ProjectDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.model.ModelFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;

@AllArgsConstructor
public class ChangesetFactory {
    private final ModelFactory modelFactory;

    @SneakyThrows
    public Changeset create(ChangesetDto changeset, ProjectDto project, MediaType contentType,
            Supplier<Changeset> parentLoader) {

        var baseModel = modelFactory.createBaseModel(changeset, contentType);
        var currentModel = baseModel;

        List<Operation> operations = new ArrayList<>(changeset.getOperations().size());
        for(OperationWithPatchesDto operation: changeset.getOperations()) {
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
                .parentLoader(parentLoader)
                .baseModel(new Model(baseModel.toDto().getEntities()))
                .project(project.getName())
                .organization(project.getOrganization())
                .entities(changeset.getEntities())
                .operations(operations)
                .build();
    }
}
