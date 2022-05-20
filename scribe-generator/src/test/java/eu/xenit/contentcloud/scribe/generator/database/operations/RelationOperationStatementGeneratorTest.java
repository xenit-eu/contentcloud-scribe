package eu.xenit.contentcloud.scribe.generator.database.operations;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Model;
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
import java.util.Map;
import org.junit.jupiter.api.Test;

class RelationOperationStatementGeneratorTest {
    StatementGenerator generator = new RelationOperationStatementGenerator();
    @Test
    void addRelationOneToOne() {
        var statements = generator.generate(new Operation(
                "add-relation",
                Map.of(
                        "source-entity", "Invoice",
                        "target-entity", "Party",
                        "relation-name", "sender",
                        "cardinality", "ONE_TO_ONE",
                        "required", true
                ),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("sender")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        CreateColumnStatement.builder()
                                .table("invoice")
                                .column("sender")
                                .foreignKey("party", "id")
                                .dataType(DataType.UUID)
                                .nullable(false)
                                .build()
                );
    }

    @Test
    void addRelationOneToMany() {
        var statements = generator.generate(new Operation(
                "add-relation",
                Map.of(
                        "source-entity", "Invoice",
                        "target-entity", "Party",
                        "relation-name", "senders",
                        "cardinality", "ONE_TO_MANY",
                        "required", true
                ),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        CreateColumnStatement.builder()
                                .table("party")
                                .column("_invoice_id__senders")
                                .foreignKey("invoice", "id")
                                .dataType(DataType.UUID)
                                .nullable(false)
                                .build()
                );
    }

    @Test
    void addRelationManyToOne() {
        var statements = generator.generate(new Operation(
                "add-relation",
                Map.of(
                        "source-entity", "Invoice",
                        "target-entity", "Party",
                        "relation-name", "senders",
                        "cardinality", "MANY_TO_ONE",
                        "required", true
                ),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manySourcePerTarget(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        CreateColumnStatement.builder()
                                .table("invoice")
                                .column("senders")
                                .foreignKey("party", "id")
                                .dataType(DataType.UUID)
                                .nullable(false)
                                .build()
                );
    }

    @Test
    void addRelationManyToMany() {
        var statements = generator.generate(new Operation(
                "add-relation",
                Map.of(
                        "source-entity", "Invoice",
                        "target-entity", "Party",
                        "relation-name", "senders",
                        "cardinality", "MANY_TO_MANY",
                        "required", false
                ),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(false)
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        CreateTableStatement.builder()
                                        .table("_join_invoice__senders__party")
                                                .build(),
                        CreateColumnStatement.builder()
                                .table("_join_invoice__senders__party")
                                .column("invoice_id")
                                .foreignKey("invoice", "id")
                                .dataType(DataType.UUID)
                                .nullable(false)
                                .build(),
                        CreateColumnStatement.builder()
                                .table("_join_invoice__senders__party")
                                .column("party_id")
                                .foreignKey("party", "id")
                                .dataType(DataType.UUID)
                                .nullable(false)
                                .build(),
                        CreateIndexStatement.builder()
                                .name("_join_invoice__senders__party_idx")
                                .table("_join_invoice__senders__party")
                                .column("invoice_id")
                                .column("party_id")
                                .unique(true)
                                .build()
                );
    }

    @Test
    void removeRelationOneToOne() {
        var statements = generator.generate(new Operation(
                "remove-relation",
                Map.of(
                        "entity-name", "Invoice",
                        "relation-name", "sender"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("sender")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("sender")
                                .build()
                );
    }

    @Test
    void removeRelationOneToMany() {
        var statements = generator.generate(new Operation(
                "remove-relation",
                Map.of(
                        "entity-name", "Invoice",
                        "relation-name", "senders"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("party")
                                .column("_invoice_id__senders")
                                .build()
                );
    }

    @Test
    void removeRelationManyToOne() {
        var statements = generator.generate(new Operation(
                "remove-relation",
                Map.of(
                        "entity-name", "Invoice",
                        "relation-name", "senders"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manySourcePerTarget(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("senders")
                                .build()
                );
    }

    @Test
    void removeRelationManyToMany() {
        var statements = generator.generate(new Operation(
                "remove-relation",
                Map.of(
                        "entity-name", "Invoice",
                        "relation-name", "senders"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(false)
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropTableStatement.builder()
                                .table("_join_invoice__senders__party")
                                .build()
                );
    }

    @Test
    void removeSourceEntityOneToOne() {
        var statements = generator.generate(new Operation(
                "remove-entity",
                Map.of(
                        "entity-name", "Invoice"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("sender")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("sender")
                                .build()
                );
    }

    @Test
    void removeSourceEntityOneToMany() {
        var statements = generator.generate(new Operation(
                "remove-entity",
                Map.of(
                        "entity-name", "Invoice"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("party")
                                .column("_invoice_id__senders")
                                .build()
                );
    }

    @Test
    void removeSourceEntityManyToOne() {
        var statements = generator.generate(new Operation(
                "remove-entity",
                Map.of(
                        "entity-name", "Invoice"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manySourcePerTarget(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("senders")
                                .build()
                );
    }

    @Test
    void removeSourceEntityManyToMany() {
        var statements = generator.generate(new Operation(
                "remove-entity",
                Map.of(
                        "entity-name", "Invoice"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(false)
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropTableStatement.builder()
                                .table("_join_invoice__senders__party")
                                .build()
                );
    }

    @Test
    void removeTargetEntityOneToOne() {
        var statements = generator.generate(new Operation(
                "remove-entity",
                Map.of(
                        "entity-name", "Party"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("sender")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("sender")
                                .build()
                );
    }

    @Test
    void removeTargetEntityOneToMany() {
        var statements = generator.generate(new Operation(
                "remove-entity",
                Map.of(
                        "entity-name", "Party"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("party")
                                .column("_invoice_id__senders")
                                .build()
                );
    }

    @Test
    void removeTargetEntityManyToOne() {
        var statements = generator.generate(new Operation(
                "remove-entity",
                Map.of(
                        "entity-name", "Party"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manySourcePerTarget(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("senders")
                                .build()
                );
    }

    @Test
    void removeTargetEntityManyToMany() {
        var statements = generator.generate(new Operation(
                "remove-entity",
                Map.of(
                        "entity-name", "Party"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(false)
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("Invoice").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        DropTableStatement.builder()
                                .table("_join_invoice__senders__party")
                                .build()
                );
    }

    @Test
    void renameSourceEntityOneToOne() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Invoice",
                        "new-entity-name", "Factuur"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("sender")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Factuur")
                                .relation(Relation.builder()
                                        .name("sender")
                                        .source("Factuur")
                                        .target("Party")
                                        .required(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements).isEmpty();
    }

    @Test
    void renameSourceEntityOneToMany() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Invoice",
                        "new-entity-name", "Factuur"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Factuur")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Factuur")
                                        .target("Party")
                                        .required(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements).containsExactly(
                RenameColumnStatement.builder()
                        .table("party")
                        .oldColumnName("_invoice_id__senders")
                        .newColumnName("_factuur_id__senders")
                        .build()
        );
    }

    @Test
    void renameSourceEntityManyToOne() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Invoice",
                        "new-entity-name", "Factuur"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manySourcePerTarget(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Factuur")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Factuur")
                                        .target("Party")
                                        .required(true)
                                        .manySourcePerTarget(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements).isEmpty();
    }

    @Test
    void renameSourceEntityManyToMany() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Invoice",
                        "new-entity-name", "Factuur"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(false)
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Factuur")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Factuur")
                                        .target("Party")
                                        .required(false)
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        RenameTableStatement.builder()
                                .oldName("_join_invoice__senders__party")
                                .newName("_join_factuur__senders__party")
                                .build(),
                        RenameColumnStatement.builder()
                                .table("_join_factuur__senders__party")
                                .oldColumnName("invoice_id")
                                .newColumnName("factuur_id")
                                .build(),
                        RenameIndexStatement.builder()
                                .oldName("_join_invoice__senders__party_idx")
                                .newName("_join_factuur__senders__party_idx")
                                .build()
                );
    }

    @Test
    void renameTargetEntityOneToOne() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Party",
                        "new-entity-name", "CounterParty"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("sender")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("sender")
                                        .source("Invoice")
                                        .target("CounterParty")
                                        .required(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("CounterParty").build())
                        .build()
        ));

        assertThat(statements).isEmpty();
    }

    @Test
    void renameTargetEntityOneToMany() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Party",
                        "new-entity-name", "CounterParty"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("CounterParty")
                                        .required(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("CounterParty").build())
                        .build()
        ));

        assertThat(statements).isEmpty();
    }

    @Test
    void renameTargetEntityManyToOne() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Party",
                        "new-entity-name", "CounterParty"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(true)
                                        .manySourcePerTarget(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("CounterParty")
                                        .required(true)
                                        .manySourcePerTarget(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("CounterParty").build())
                        .build()
        ));

        assertThat(statements).isEmpty();
    }

    @Test
    void renameTargetEntityManyToMany() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Party",
                        "new-entity-name", "CounterParty"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("Party")
                                        .required(false)
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("Party").build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .relation(Relation.builder()
                                        .name("senders")
                                        .source("Invoice")
                                        .target("CounterParty")
                                        .required(false)
                                        .manySourcePerTarget(true)
                                        .manyTargetPerSource(true)
                                        .build())
                                .build())
                        .entity(Entity.builder().name("CounterParty").build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(
                        RenameTableStatement.builder()
                                .oldName("_join_invoice__senders__party")
                                .newName("_join_invoice__senders__counter_party")
                                .build(),
                        RenameColumnStatement.builder()
                                .table("_join_invoice__senders__counter_party")
                                .oldColumnName("party_id")
                                .newColumnName("counter_party_id")
                                .build(),
                        RenameIndexStatement.builder()
                                .oldName("_join_invoice__senders__party_idx")
                                .newName("_join_invoice__senders__counter_party_idx")
                                .build()
                );
    }
}