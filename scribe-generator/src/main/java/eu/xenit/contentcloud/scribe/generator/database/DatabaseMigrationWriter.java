package eu.xenit.contentcloud.scribe.generator.database;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.operations.StatementGenerator;
import eu.xenit.contentcloud.scribe.generator.database.sql.CommentStatement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
class DatabaseMigrationWriter {

    private final StatementGenerator generator;

    public void writeOperation(Writer writer, Operation operation) throws IOException {
        try {
            generator.generate(operation).forEachOrdered(statement -> {
                try {
                    writer.write(statement.toSql() + "\n");
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch(UncheckedIOException uncheckedIOException) {
            throw uncheckedIOException.getCause();
        }
    }

}
