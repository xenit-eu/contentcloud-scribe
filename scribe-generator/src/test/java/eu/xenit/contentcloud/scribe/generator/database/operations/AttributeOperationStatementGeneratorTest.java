package eu.xenit.contentcloud.scribe.generator.database.operations;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.contentcloud.scribe.changeset.Attribute;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.changeset.Model;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateColumnStatement.DataType;
import eu.xenit.contentcloud.scribe.generator.database.sql.CreateIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.DropColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameColumnStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.RenameIndexStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.ErrorStatement;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AttributeOperationStatementGeneratorTest {
    private final StatementGenerator generator = new CommentFilteringStatementGenerator(new AttributeOperationStatementGenerator());

    @ParameterizedTest
    @CsvSource({
            "LONG,BIGINT",
            "DATETIME,DATETIME",
            "STRING,TEXT"
    })
    void addAttributeSimple(String modelType, String dbType) {
        var statements = generator.generate(new Operation(
                "add-attribute",
                Map.of(
                        "entity-name", "Invoice",
                        "attribute-name", "Amount",
                        "type", modelType,
                        "naturalId", false,
                        "indexed", true,
                        "unique", false,
                        "required", false
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").type(modelType).indexed(true).build())
                                .build())
                        .build()
        ));

        assertThat(statements)
                .containsExactly(CreateColumnStatement.builder()
                                .table("invoice")
                                .column("amount")
                                .dataType(DataType.valueOf(dbType))
                                .nullable(true)
                                .build(),
                        CreateIndexStatement.builder()
                                .table("invoice")
                                .column("amount")
                                .build()
                );
    }

    @Test
    void addAttributeContent() {
        var statements = generator.generate(new Operation(
                "add-attribute",
                Map.of(
                        "entity-name", "Invoice",
                        "attribute-name", "Scan",
                        "type", "CONTENT",
                        "naturalId", false,
                        "indexed", true,
                        "unique", false,
                        "required", false
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Scan").content().build())
                                .build())
                        .build()
        ));

        assertThat(statements)
                .containsExactlyInAnyOrder(CreateColumnStatement.builder()
                                .table("invoice")
                                .column("scan_id")
                                .dataType(DataType.TEXT)
                                .nullable(true)
                                .build(),
                        CreateColumnStatement.builder()
                                .table("invoice")
                                .column("scan_length")
                                .dataType(DataType.BIGINT)
                                .nullable(true)
                                .build(),
                        CreateColumnStatement.builder()
                                .table("invoice")
                                .column("scan_mimetype")
                                .dataType(DataType.TEXT)
                                .nullable(true)
                                .build(),
                        CreateColumnStatement.builder()
                                .table("invoice")
                                .column("scan_filename")
                                .dataType(DataType.TEXT)
                                .nullable(true)
                                .build()
                );
    }

    @Test
    void addAttributeUnknownType() {
        var statements = generator.generate(new Operation(
                "add-attribute",
                Map.of(
                        "entity-name", "Invoice",
                        "attribute-name", "Scan",
                        "type", "UNKNOWN",
                        "naturalId", false,
                        "indexed", true,
                        "unique", false,
                        "required", false
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Scan").type("UNKNOWN").build())
                                .build())
                        .build()
        ));

        assertThat(statements)
                .hasAtLeastOneElementOfType(ErrorStatement.class);
    }

    @Test
    void renameAttributeSimple() {
        var statements = generator.generate(new Operation(
                "rename-attribute",
                Map.of(
                        "entity-name", "Invoice",
                        "old-attribute-name", "Amount",
                        "new-attribute-name", "DebitAmount"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").string().indexed(true).build())
                                .build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("DebitAmount").string().indexed(true).build())
                                .build())
                        .build()
        ));

        assertThat(statements)
                .containsExactlyInAnyOrder(RenameColumnStatement.builder()
                                .table("invoice")
                                .oldColumnName("amount")
                                .newColumnName("debit_amount")
                                .build(),
                        RenameIndexStatement.builder()
                                .oldName("invoice_amount_idx")
                                .newName("invoice_debit_amount_idx")
                                .build()
                );
    }

    @Test
    void renameAttributeContent() {
        var statements = generator.generate(new Operation(
                "rename-attribute",
                Map.of(
                        "entity-name", "Invoice",
                        "old-attribute-name", "Scan",
                        "new-attribute-name", "Image"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").string().indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().build())
                                .build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").string().indexed(true).build())
                                .attribute(Attribute.builder("Image").content().build())
                                .build())
                        .build()
        ));

        assertThat(statements)
                .containsExactlyInAnyOrder(
                        RenameColumnStatement.builder()
                                .table("invoice")
                                .oldColumnName("scan_id")
                                .newColumnName("image_id")
                                .build(),
                        RenameColumnStatement.builder()
                                .table("invoice")
                                .oldColumnName("scan_length")
                                .newColumnName("image_length")
                                .build(),
                        RenameColumnStatement.builder()
                                .table("invoice")
                                .oldColumnName("scan_mimetype")
                                .newColumnName("image_mimetype")
                                .build(),
                        RenameColumnStatement.builder()
                                .table("invoice")
                                .oldColumnName("scan_filename")
                                .newColumnName("image_filename")
                                .build()
                );
    }

    @Test
    void deleteAttributeSimple() {
        var statements = generator.generate(new Operation(
                "delete-attribute",
                Map.of(
                        "entity-name", "Invoice",
                        "attribute-name", "Amount"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").string().indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().build())
                                .build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .build())
                        .build()
        ));

        assertThat(statements)
                .containsExactlyInAnyOrder(
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("amount")
                                .build()
                );
    }

    @Test
    void deleteAttributeContent() {
        var statements = generator.generate(new Operation(
                "delete-attribute",
                Map.of(
                        "entity-name", "Invoice",
                        "attribute-name", "Scan"
                ),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").string().indexed(true).build())
                                .attribute(Attribute.builder("Scan").content().build())
                                .build())
                        .build(),
                Model.builder()
                        .entity(Entity.builder()
                                .name("Invoice")
                                .attribute(Attribute.builder("Amount").string().indexed(true).build())
                                .build())
                        .build()
        ));

        assertThat(statements)
                .containsExactlyInAnyOrder(
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("scan_id")
                                .build(),
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("scan_length")
                                .build(),
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("scan_mimetype")
                                .build(),
                        DropColumnStatement.builder()
                                .table("invoice")
                                .column("scan_filename")
                                .build()
                );
    }
}