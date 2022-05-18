package eu.xenit.contentcloud.scribe.generator.database.operations;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Model;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateTableStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.DropTableStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameTableStatement;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EntityOperationStatementGeneratorTest {
    private final StatementGenerator generator = new CommentFilteringStatementGenerator(new EntityOperationStatementGenerator());

    @Test
    void addEntity() {
        var statements = generator.generate(new Operation(
                "add-entity",
                Map.of(
                        "entity-name", "Invoice"
                ),
                Model.builder()
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("invoice").build())
                        .build()
        ));

        assertThat(statements).containsExactly(
                CreateTableStatement.builder()
                        .table("invoice")
                        .build()
        );
    }

    @Test
    void renameEntity() {
        var statements = generator.generate(new Operation(
                "rename-entity",
                Map.of(
                        "old-entity-name", "Invoice",
                        "new-entity-name", "OutgoingInvoice"
                ),
                Model.builder()
                        .entity(Entity.builder().name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Identifier").number().unique(true).naturalId(true).build())
                                .attribute(Attribute.builder("Scan").content().build())
                                .build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder().name("OutgoingInvoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Identifier").number().unique(true).naturalId(true).build())
                                .attribute(Attribute.builder("Scan").content().build())
                                .build())
                        .build()
        ));

        assertThat(statements).containsExactlyInAnyOrder(
                RenameTableStatement.builder()
                        .oldName("invoice")
                        .newName("outgoing_invoice")
                        .build(),
                RenameIndexStatement.builder()
                        .oldName("invoice_amount_idx")
                        .newName("outgoing_invoice_amount_idx")
                        .build(),
                RenameIndexStatement.builder()
                        .oldName("invoice_identifier_idx")
                        .newName("outgoing_invoice_identifier_idx")
                        .build()
        );
    }

    @Test
    void deleteEntity() {
        var statements = generator.generate(new Operation(
                "delete-entity",
                Map.of(
                        "entity-name", "Invoice"
                ),
                Model.builder()
                        .entity(Entity.builder().name("Invoice")
                                .attribute(Attribute.builder("Amount").number().indexed(true).build())
                                .attribute(Attribute.builder("Identifier").number().unique(true).naturalId(true).build())
                                .attribute(Attribute.builder("Scan").content().build())
                                .build())
                        .build(),
                Model.builder()
                        .build()
        ));

        assertThat(statements).containsExactlyInAnyOrder(
                DropTableStatement.builder()
                        .table("invoice")
                        .build()
        );
    }
}