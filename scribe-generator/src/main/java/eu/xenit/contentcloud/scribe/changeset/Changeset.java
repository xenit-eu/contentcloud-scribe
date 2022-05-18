package eu.xenit.contentcloud.scribe.changeset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Builder
@AllArgsConstructor
public class Changeset {

    String project;
    String organization;

    @NonNull
    List<Entity> entities;

    @NonNull
    List<Operation> operations;

    public static class ChangesetBuilder {
        @Setter
        @Accessors(fluent = true)
        private Model baseModel;
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
