package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertAttributeNameToColumnName;
import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertEntityNameToTableName;
import static eu.xenit.contentcloud.scribe.generator.database.sql.CommentStatement.comment;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateColumnStatement.DataType;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.DropColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.List;
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

    private static final Map<String, DataType> DATA_TYPES = Map.of(
            "STRING", DataType.TEXT,
            "DATETIME", DataType.DATETIME,
            "LONG", DataType.BIGINT
    );

    private static final List<String> CONTENT_TYPE_FIELD_SUFFIXES = List.of(
            "_id",
            "_length",
            "_mimetype",
            "_filename"
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
        if(operationDataType.equals("CONTENT")) {
            return addContentAttribute(operation);
        }
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

    private Stream<Statement> addContentAttribute(Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperty("entity-name"));
        var columnName = convertAttributeNameToColumnName((String) operation.getProperty("attribute-name"));
        var nullable = Boolean.FALSE.equals(operation.getProperty("required"));

        return Stream.of(
                CreateColumnStatement.builder()
                        .table(tablename)
                        .column(columnName+"_id")
                        .dataType(DataType.TEXT)
                        .nullable(nullable)
                        .build(),
                CreateColumnStatement.builder()
                        .table(tablename)
                        .column(columnName+"_length")
                        .dataType(DataType.BIGINT)
                        .nullable(nullable)
                        .build(),
                CreateColumnStatement.builder()
                        .table(tablename)
                        .column(columnName+"_mimetype")
                        .dataType(DataType.TEXT)
                        .nullable(nullable)
                        .build(),
                CreateColumnStatement.builder()
                        .table(tablename)
                        .column(columnName+"_filename")
                        .dataType(DataType.TEXT)
                        .nullable(nullable)
                        .build()
        );

    }

    private Stream<Statement> renameAttribute(Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        var oldColumnName = convertAttributeNameToColumnName((String) operation.getProperties().get("old-attribute-name"));
        var newColumnName = convertAttributeNameToColumnName((String) operation.getProperties().get("new-attribute-name"));

        var streamBuilder = Stream.<Statement>builder();

        var attribute = operation.getBeforeModel()
                .getEntityAttribute((String) operation.getProperty("entity-name"), (String) operation.getProperty("old-attribute-name"))
                .orElseThrow();

        if(attribute.getType().equals("CONTENT")) {
            return CONTENT_TYPE_FIELD_SUFFIXES.stream()
                    .map(fieldSuffix -> RenameColumnStatement.builder()
                            .table(tablename)
                            .oldColumnName(oldColumnName+fieldSuffix)
                            .newColumnName(newColumnName+fieldSuffix)
                            .build()
                    );
        }


        var renameColumn = RenameColumnStatement.builder()
                .table(tablename)
                .oldColumnName(oldColumnName)
                .newColumnName(newColumnName)
                .build();
        streamBuilder.add(renameColumn);


        if(attribute.isIndexed() || attribute.isUnique()) {
            streamBuilder.add(RenameIndexStatement.forColumn(renameColumn));
        }

        return streamBuilder.build();
    }

    private Stream<Statement> deleteAttribute(Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        var columnName = convertAttributeNameToColumnName((String) operation.getProperties().get("attribute-name"));
        var attribute = operation.getBeforeModel()
                .getEntityAttribute((String) operation.getProperty("entity-name"), (String) operation.getProperty("attribute-name"))
                .orElseThrow();

        if(attribute.getType().equals("CONTENT")) {
            return CONTENT_TYPE_FIELD_SUFFIXES.stream()
                    .map(fieldSuffix -> DropColumnStatement.builder()
                            .table(tablename)
                            .column(columnName+fieldSuffix)
                            .build()
                    );
        }
        return Stream.of(DropColumnStatement.builder()
                .table(tablename)
                .column(columnName)
                .build());
    }
}
