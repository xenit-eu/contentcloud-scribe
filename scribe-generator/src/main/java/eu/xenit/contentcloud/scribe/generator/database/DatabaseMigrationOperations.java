package eu.xenit.contentcloud.scribe.generator.database;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.operations.OperationWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ParsingUtils;

@Slf4j
@AllArgsConstructor
class DatabaseMigrationOperations implements OperationWriter {

    private final Set<OperationWriter> operationWriters;

    public boolean writeOperation(Writer writer, Operation operation) throws IOException {
        boolean result = false;
        for (OperationWriter operationWriter : operationWriters) {
            result |= operationWriter.writeOperation(writer, operation);
        }

        if(!result) {
            log.warn("Unsupported operation {}", operation);
            writer.write("-- Unsupported operation "+operation +" not written.\n");
        }
        return result;
    }

}
