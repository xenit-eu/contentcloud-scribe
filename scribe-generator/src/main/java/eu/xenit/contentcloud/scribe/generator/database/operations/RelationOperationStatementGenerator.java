package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertAttributeNameToColumnName;
import static eu.xenit.contentcloud.scribe.generator.database.operations.NamingUtils.convertEntityNameToTableName;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.changeset.Relation;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateColumnStatement.DataType;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateTableStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.DropColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.DropTableStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameTableStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

public class RelationOperationStatementGenerator implements StatementGenerator {
    interface RelationStatementGenerator {
        Stream<Statement> create(Relation relation);
        Stream<Statement> renameSourceEntity(Relation oldRelation, Relation newRelation);
        Stream<Statement> renameTargetEntity(Relation oldRelation, Relation newRelation);
        Stream<Statement> delete(Relation relation);
    }

    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private enum RelationType {
        ONE_TO_ONE(false, false, new SourceColumnStatementGenerator()),
        ONE_TO_MANY(false, true, new TargetColumnStatementGenerator()),
        MANY_TO_ONE(true, false, new SourceColumnStatementGenerator()),
        MANY_TO_MANY(true, true, new JoinTableStatementGenerator());

        boolean manySourcePerTarget;
        boolean manyTargetPerSource;
        RelationStatementGenerator generator;

        public static RelationType forRelation(Relation relation) {
            return Arrays.stream(values())
                    .filter(rt -> Objects.equals(rt.manySourcePerTarget, relation.isManySourcePerTarget()))
                    .filter(rt -> Objects.equals(rt.manyTargetPerSource, relation.isManyTargetPerSource()))
                    .findFirst()
                    .orElseThrow();
        }
    }

    @Override
    public Stream<Statement> generate(Operation operation) {
        switch(operation.getType()) {
            case "add-relation": {
                var relation = operation.getAfterModel()
                        .getEntityRelation((String) operation.getProperty("source-entity"),
                                (String) operation.getProperty("relation-name")).orElseThrow();
                return RelationType.forRelation(relation).generator.create(relation);
            }
            case "delete-relation": {
                var relation = operation.getBeforeModel().getEntityRelation((String)operation.getProperty("entity-name"), (String)operation.getProperty("relation-name")).orElseThrow();
                return RelationType.forRelation(relation).generator.delete(relation);
            }
            case "delete-entity": {
                var removedEntity = operation.getBeforeModel().getEntity((String)operation.getProperty("entity-name")).orElseThrow();
                return operation.getBeforeModel().getEntities()
                        .stream()
                        .flatMap(entity -> entity.getRelations().stream())
                        .filter(relation -> Objects.equals(relation.getSource(), removedEntity.getName()) || Objects.equals(relation.getTarget(), removedEntity.getName()))
                        .flatMap(relation -> {
                            return RelationType.forRelation(relation).generator.delete(relation);
                        });
            }
            case "rename-entity": {
                var sourceEntity = operation.getBeforeModel().getEntity((String)operation.getProperty("old-entity-name")).orElseThrow();
                var sourceEntityOps = sourceEntity.getRelations().stream().flatMap(relation -> {
                    var newRelation = operation.getAfterModel().getEntityRelation((String)operation.getProperty("new-entity-name"), relation.getName()).orElseThrow();
                    return RelationType.forRelation(relation).generator.renameSourceEntity(relation, newRelation);
                });

                var targetEntityOps = operation.getBeforeModel().getEntities()
                        .stream()
                        .flatMap(entity -> entity.getRelations().stream())
                        .filter(relation -> Objects.equals(relation.getTarget(), sourceEntity.getName()))
                        .flatMap(relation -> {
                            var newRelation = operation.getAfterModel().getEntityRelation(relation.getSource(), relation.getName()).orElseThrow();
                            return RelationType.forRelation(relation).generator.renameTargetEntity(relation, newRelation);
                        });
                return Stream.concat(
                        sourceEntityOps,
                        targetEntityOps
                );
            }
            default:
                return Stream.empty();
        }
    }

    private static class SourceColumnStatementGenerator implements RelationStatementGenerator {

        @Override
        public Stream<Statement> create(Relation relation) {
            String tableName = convertEntityNameToTableName(relation.getSource());
            String targetTableName = convertEntityNameToTableName(relation.getTarget());
            String columnName = convertEntityNameToTableName(relation.getName());
            return Stream.of(
                    CreateColumnStatement.builder()
                            .table(tableName)
                            .column(columnName)
                            .foreignKey(targetTableName, "id")
                            .nullable(!relation.isRequired())
                            .dataType(DataType.UUID)
                            .build()
            );
        }

        @Override
        public Stream<Statement> renameSourceEntity(Relation oldRelation, Relation newRelation) {
            // Source entity rename will already rename the table, no changes to the column necessary
            return Stream.empty();
        }

        @Override
        public Stream<Statement> renameTargetEntity(Relation oldRelation, Relation newRelation) {
            // Target entity rename will already rename the foreign key target, no changes to it are necessary
            return Stream.empty();
        }

        @Override
        public Stream<Statement> delete(Relation relation) {
            String tableName = convertEntityNameToTableName(relation.getSource());
            String columnName = convertEntityNameToTableName(relation.getName());
            return Stream.of(
                    DropColumnStatement
                            .builder()
                            .table(tableName)
                            .column(columnName)
                            .build()
            );
        }
    }

    private static class TargetColumnStatementGenerator implements RelationStatementGenerator {

        private static String columnName(Relation relation) {
            return "_"+ convertEntityNameToTableName(relation.getSource())
                    +"_id__"+ convertAttributeNameToColumnName(relation.getName());
        }

        @Override
        public Stream<Statement> create(Relation relation) {
            String tableName = convertEntityNameToTableName(relation.getTarget());
            String sourceName = convertEntityNameToTableName(relation.getSource());
            return Stream.of(
                    CreateColumnStatement.builder()
                            .table(tableName)
                            .column(columnName(relation))
                            .foreignKey(sourceName, "id")
                            .dataType(DataType.UUID)
                            .build()
            );
        }

        @Override
        public Stream<Statement> renameSourceEntity(Relation oldRelation, Relation newRelation) {
            String tableName = convertEntityNameToTableName(newRelation.getTarget());

            return Stream.of(
                    RenameColumnStatement.builder()
                            .table(tableName)
                            .oldColumnName(columnName(oldRelation))
                            .newColumnName(columnName(newRelation))
                            .build()
            );
        }

        @Override
        public Stream<Statement> renameTargetEntity(Relation oldRelation, Relation newRelation) {
            // Target entity rename will already rename the table, no changes to the column necessary
            return Stream.empty();
        }

        @Override
        public Stream<Statement> delete(Relation relation) {
            String tableName = convertEntityNameToTableName(relation.getTarget());
            return Stream.of(
                    DropColumnStatement.builder()
                            .table(tableName)
                            .column(columnName(relation))
                            .build()
            );
        }
    }

    private static class JoinTableStatementGenerator implements RelationStatementGenerator {
        private static String joinTableName(Relation relation) {
            return "_join_"+ convertEntityNameToTableName(relation.getSource()) +
                    "__"+ convertAttributeNameToColumnName(relation.getName())+
                    "__"+ convertEntityNameToTableName(relation.getTarget());

        }
        @Override
        public Stream<Statement> create(Relation relation) {
            String sourceName = convertEntityNameToTableName(relation.getSource());
            String targetName = convertEntityNameToTableName(relation.getTarget());

            String tableName = joinTableName(relation);

            return Stream.of(
                    CreateTableStatement
                            .builder()
                            .table(tableName)
                            .build(),
                    CreateColumnStatement
                            .builder()
                            .table(tableName)
                            .column(sourceName+"_id")
                            .dataType(DataType.UUID)
                            .foreignKey(sourceName, "id")
                            .build(),
                    CreateColumnStatement
                            .builder()
                            .table(tableName)
                            .column(targetName+"_id")
                            .dataType(DataType.UUID)
                            .foreignKey(targetName, "id")
                            .build(),
                    CreateIndexStatement.builder()
                            .name(tableName+"_idx")
                            .table(tableName)
                            .column(sourceName+"_id")
                            .column(targetName+"_id")
                            .unique(true)
                            .build()
            );
        }

        @Override
        public Stream<Statement> renameSourceEntity(Relation oldRelation, Relation newRelation) {
            String oldTableName = joinTableName(oldRelation);
            String newTableName = joinTableName(newRelation);
            String oldSourceName = convertEntityNameToTableName(oldRelation.getSource());
            String newSourceName = convertEntityNameToTableName(newRelation.getSource());
            return Stream.of(
                    RenameTableStatement.builder()
                            .oldName(oldTableName)
                            .newName(newTableName)
                            .build(),
                    RenameColumnStatement.builder()
                            .table(newTableName)
                            .oldColumnName(oldSourceName+"_id")
                            .newColumnName(newSourceName+"_id")
                            .build(),
                    RenameIndexStatement.builder()
                            .oldName(oldTableName+"_idx")
                            .newName(newTableName+"_idx")
                            .build()
            );
        }

        @Override
        public Stream<Statement> renameTargetEntity(Relation oldRelation, Relation newRelation) {
            String oldTableName = joinTableName(oldRelation);
            String newTableName = joinTableName(newRelation);
            String oldTargetName = convertEntityNameToTableName(oldRelation.getTarget());
            String newTargetName = convertEntityNameToTableName(newRelation.getTarget());
            return Stream.of(
                    RenameTableStatement.builder()
                            .oldName(oldTableName)
                            .newName(newTableName)
                            .build(),
                    RenameColumnStatement.builder()
                            .table(newTableName)
                            .oldColumnName(oldTargetName+"_id")
                            .newColumnName(newTargetName+"_id")
                            .build(),
                    RenameIndexStatement.builder()
                            .oldName(oldTableName+"_idx")
                            .newName(newTableName+"_idx")
                            .build()
            );
        }

        @Override
        public Stream<Statement> delete(Relation relation) {
            return Stream.of(
                    DropTableStatement.builder()
                            .table(joinTableName(relation))
                            .build()
            );
        }
    }

}
