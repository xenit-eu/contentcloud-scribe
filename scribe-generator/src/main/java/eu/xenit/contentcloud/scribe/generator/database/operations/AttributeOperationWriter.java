package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertAttributeNameToColumnName;
import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertEntityNameToTableName;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttributeOperationWriter implements OperationWriter {
    private final Map<String, OperationWriter> OPERATIONS = Map.of(
            "add-attribute", this::addAttribute,
            "rename-attribute", this::renameAttribute,
            "delete-attribute", this::deleteAttribute
    );

    private static final Map<String, String> DATA_TYPES = Map.of(
            "STRING", "text",
            "DATETIME", "datetime",
            "LONG", "bigint"
    );

    @Override
    public boolean writeOperation(Writer writer, Operation operation) throws IOException {
        OperationWriter operationWriter = OPERATIONS.get(operation.getType());
        if(operationWriter == null) {
            return false;
        }
        return operationWriter.writeOperation(writer, operation);
    }

    private boolean addAttribute(Writer writer, Operation operation) throws IOException {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        var columnName = convertAttributeNameToColumnName((String) operation.getProperties().get("attribute-name"));
        var operationDataType = (String)operation.getProperties().get("type");
        var dataType = DATA_TYPES.get(operationDataType);

        if(dataType == null) {
            log.warn("Unsupported data type {}", operationDataType);
            writer.write("-- Unsupported data type "+operationDataType+" not written.\n");
            return false ;
        }

        writer.write("ALTER TABLE "+tablename+" ADD COLUMN "+columnName+" "+dataType);

        if(Boolean.TRUE.equals(operation.getProperties().get("required"))) {
            writer.write(" NOT NULL");
        } else  {
            writer.write(" NULL");
        }
        writer.write(";\n");

        String attributeId = operation.getProperties().get("attribute-id").toString();

        if(Boolean.TRUE.equals(operation.getProperties().get("unique"))) {
            writer.write("CREATE UNIQUE INDEX CONCURRENTLY \"" +attributeId + "_uniq\" ON " + tablename + "(" + columnName + ");\n");
        }
        if(Boolean.TRUE.equals(operation.getProperties().get("indexed"))) {
            writer.write("CREATE INDEX CONCURRENTLY \""+attributeId+"_idx\" ON "+tablename+"("+columnName+");\n");
        }
        return true;
    }

    private boolean renameAttribute(Writer writer, Operation operation) throws IOException {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        var oldColumnName = convertAttributeNameToColumnName((String) operation.getProperties().get("old-attribute-name"));
        var newColumnName = convertAttributeNameToColumnName((String) operation.getProperties().get("new-attribute-name"));
        writer.write("ALTER TABLE "+tablename+" RENAME COLUMN "+oldColumnName+" TO "+newColumnName+";\n");
        return true;
    }

    private boolean deleteAttribute(Writer writer, Operation operation) throws IOException {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        var columnName = convertAttributeNameToColumnName((String) operation.getProperties().get("attribute-name"));
        writer.write("ALTER TABLE "+tablename+" DROP COLUMN "+columnName+";\n");
        return true;
    }
}
