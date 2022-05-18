package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertAttributeNameToColumnName;
import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertEntityNameToTableName;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateTableStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.DropTableStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameTableStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.Map;
import java.util.stream.Stream;

public class EntityOperationStatementGenerator implements StatementGenerator {

    private final Map<String, StatementGenerator> OPERATIONS = Map.of(
            "add-entity", this::addEntity,
            "rename-entity", this::renameEntity,
            "delete-entity", this::deleteEntity
    );

    @Override
    public Stream<Statement> generate(Operation operation) {
        StatementGenerator statementGenerator = OPERATIONS.get(operation.getType());
        if(statementGenerator == null) {
            return Stream.empty();
        }
        return statementGenerator.generate(operation);
    }

    private Stream<Statement> addEntity(Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        return Stream.of(CreateTableStatement.builder()
                .table(tablename)
                .build());
    }

    private Stream<Statement> renameEntity(Operation operation) {
        var oldName = convertEntityNameToTableName((String) operation.getProperties().get("old-entity-name"));
        var newName = convertEntityNameToTableName((String) operation.getProperties().get("new-entity-name"));

        var streamBuilder = Stream.<Statement>builder();
        var renameTable = RenameTableStatement.builder()
                .oldName(oldName)
                .newName(newName)
                .build();
        streamBuilder.add(renameTable);

        var entity = operation.getBeforeModel()
                .getEntity((String) operation.getProperty("old-entity-name"))
                .orElseThrow();

        for (Attribute attribute : entity.getAttributes()) {
            String columnName = convertAttributeNameToColumnName(attribute.getName());
            streamBuilder.add(RenameIndexStatement.forTable(renameTable, columnName));
        }

        return streamBuilder.build();
    }

    private Stream<Statement> deleteEntity(Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        return Stream.of(DropTableStatement.builder()
                .table(tablename)
                .build());
    }
}
