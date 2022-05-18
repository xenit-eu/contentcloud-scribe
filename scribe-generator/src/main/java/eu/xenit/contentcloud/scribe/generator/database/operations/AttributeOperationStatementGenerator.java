package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertAttributeNameToColumnName;
import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertEntityNameToTableName;
import static eu.xenit.contentcloud.scribe.generator.database.sql.CommentStatement.comment;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.DropColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttributeOperationStatementGenerator implements StatementGenerator {

    private final Map<String, StatementGenerator> OPERATIONS = Map.of(
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
    public Stream<Statement> generate(Operation operation) {
        StatementGenerator statementGenerator = OPERATIONS.get(operation.getType());
        if(statementGenerator == null) {
            return Stream.empty();
        }
        return statementGenerator.generate(operation);
    }

    private Stream<Statement> addAttribute(Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperty("entity-name"));
        var columnName = convertAttributeNameToColumnName((String) operation.getProperty("attribute-name"));
        var operationDataType = (String)operation.getProperty("type");
        var dataType = DATA_TYPES.get(operationDataType);

        if(dataType == null) {
            log.warn("Unsupported data type {}", operationDataType);
            return Stream.of(comment("Unsupported data type "+operationDataType+" not written."));
        }
        var streamBuilder = Stream.<Statement>builder();
        var createColumn = CreateColumnStatement.builder()
                .table(tablename)
                .column(columnName)
                .dataType(dataType)
                .nullable(Boolean.FALSE.equals(operation.getProperty("required")))
                .build();
        streamBuilder.add(createColumn);

        if(Boolean.TRUE.equals(operation.getProperty(("unique"))) || Boolean.TRUE.equals(operation.getProperty("indexed"))) {
            var index = CreateIndexStatement.builder()
                    .forStatement(createColumn)
                    .unique(Boolean.TRUE.equals(operation.getProperty("unique")))
                    .build();
            streamBuilder.add(index);
        }

        return streamBuilder.build();
    }

    private Stream<Statement> renameAttribute(Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        var oldColumnName = convertAttributeNameToColumnName((String) operation.getProperties().get("old-attribute-name"));
        var newColumnName = convertAttributeNameToColumnName((String) operation.getProperties().get("new-attribute-name"));

        var streamBuilder = Stream.<Statement>builder();

        var renameColumn = RenameColumnStatement.builder()
                .table(tablename)
                .oldColumnName(oldColumnName)
                .newColumnName(newColumnName)
                .build();
        streamBuilder.add(renameColumn);

        var attribute = operation.getBeforeModel()
                .getEntity((String) operation.getProperty("entity-name"))
                .flatMap(entity -> entity.getAttribute((String) operation.getProperty("old-attribute-name")))
                .orElseThrow();

        if(attribute.isIndexed() || attribute.isUnique()) {
            streamBuilder.add(RenameIndexStatement.forColumn(renameColumn));
        }

        return streamBuilder.build();
    }

    private Stream<Statement> deleteAttribute(Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        var columnName = convertAttributeNameToColumnName((String) operation.getProperties().get("attribute-name"));
        return Stream.of(DropColumnStatement.builder()
                .table(tablename)
                .column(columnName)
                .build());
    }
}
