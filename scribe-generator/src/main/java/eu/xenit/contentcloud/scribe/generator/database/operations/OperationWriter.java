package eu.xenit.contentcloud.scribe.generator.database.operations;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import java.io.IOException;
import java.io.Writer;

@FunctionalInterface
public
interface OperationWriter {
    boolean writeOperation(Writer writer, Operation operation) throws IOException;
}
