package eu.xenit.contentcloud.scribe.generator.database;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import java.io.Writer;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.SneakyThrows;
import org.springframework.data.util.ParsingUtils;

class DatabaseMigrationOperations {

    static final Map<String, BiConsumer<Writer, Operation>> OPERATIONS = Map.of(
            "add-entity", DatabaseMigrationOperations::addEntity
    );

    @SneakyThrows
    static void addEntity(Writer writer, Operation operation) {
        var tablename = convertEntityNameToTableName((String) operation.getProperties().get("entity-name"));
        writer.write("CREATE TABLE \""+ tablename +"\" (id UUID PRIMARY KEY);");
    }


    private static String convertEntityNameToTableName(String name) {
        return ParsingUtils.reconcatenateCamelCase(name, "_");
    }
}
