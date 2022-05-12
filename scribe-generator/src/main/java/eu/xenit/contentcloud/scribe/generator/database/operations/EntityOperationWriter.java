package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertEntityNameToTableName;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class EntityOperationWriter implements OperationWriter {

    private final Map<String, OperationWriter> OPERATIONS = Map.of(
            "add-entity", this::addEntity,
            "rename-entity", this::renameEntity,
            "delete-entity", this::deleteEntity
    );

    @Override
    public boolean writeOperation(Writer writer, Operation operation) throws IOException {
        OperationWriter operationWriter = OPERATIONS.get(operation.getType());
        if(operationWriter == null) {
            return false;
        }
        return operationWriter.writeOperation(writer, operation);
    }

    private boolean addEntity(Writer writer, Operation operation) throws IOException {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        writer.write("CREATE TABLE "+ tablename +" (id UUID PRIMARY KEY);\n");
        return true;
    }

    private boolean renameEntity(Writer writer, Operation operation) throws IOException {
        var oldName = convertEntityNameToTableName((String) operation.getProperties().get("old-entity-name"));
        var newName = convertEntityNameToTableName((String) operation.getProperties().get("new-entity-name"));

        writer.write("ALTER TABLE "+oldName+" RENAME TO "+newName+";\n");
        return true;
    }

    private boolean deleteEntity(Writer writer, Operation operation) throws IOException {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        writer.write("DROP TABLE "+ tablename +";\n");
        return true;
    }
}
