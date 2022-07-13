package eu.xenit.contentcloud.scribe.changeset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Builder
@AllArgsConstructor
public class Changeset {

    @Getter(value = AccessLevel.NONE)
    @NonNull
    Model baseModel;

    String project;
    String organization;

    @NonNull
    List<Operation> operations;

    @Getter(AccessLevel.NONE)
    Supplier<Changeset> parentLoader;

    public Optional<Changeset> getParent() {
        return Optional.ofNullable(parentLoader)
                .map(Supplier::get);
    }

    public Model getAfterModel() {
        if(operations.isEmpty()) {
            return baseModel;
        }
        return operations.get(operations.size()-1).getAfterModel();
    }

    public List<Entity> getEntities() {
        return getAfterModel().getEntities();
    }

    public static class ChangesetBuilder {
        public ChangesetBuilder operation(String type, Map<String, Object> properties, Model nextModel) {
            var beforeModel = baseModel;
            if(this.operations != null && !this.operations.isEmpty()) {
                var lastOperation = this.operations.get(this.operations.size() - 1);
                beforeModel = lastOperation.getAfterModel();
            }
            return operation(new Operation(type, properties, beforeModel, nextModel));
        }

        public ChangesetBuilder operation(Operation operation) {
            if(this.operations == null) {
                this.operations = new ArrayList<>();
            }
            this.operations.add(operation);
            return this;
        }

    }
}
