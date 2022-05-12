package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertAttributeNameToColumnName;
import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertEntityNameToTableName;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateIndexStatement;
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
        var tablename = convertEntityNameToTableName((String) operation.getProperty("entity-name"));
        var columnName = convertAttributeNameToColumnName((String) operation.getProperty("attribute-name"));
        var operationDataType = (String)operation.getProperty("type");
        var dataType = DATA_TYPES.get(operationDataType);

        if(dataType == null) {
            log.warn("Unsupported data type {}", operationDataType);
            writer.write("-- Unsupported data type "+operationDataType+" not written.\n");
            return false ;
        }
        var createColumn = CreateColumnStatement.builder()
                .table(tablename)
                .column(columnName)
                .dataType(dataType)
                .nullable(Boolean.FALSE.equals(operation.getProperty("required")))
                .build();

        writer.write(createColumn.toSql()+"\n");

        String attributeId = operation.getProperties().get("attribute-id").toString();


        if(Boolean.TRUE.equals(operation.getProperty(("unique"))) || Boolean.TRUE.equals(operation.getProperty("indexed"))) {
            var index = CreateIndexStatement.builder()
                    .name(attributeId+"_idx")
                    .forStatement(createColumn)
                    .unique(Boolean.TRUE.equals(operation.getProperty("unique")))
                    .build();
            writer.write(index.toSql()+"\n");
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
