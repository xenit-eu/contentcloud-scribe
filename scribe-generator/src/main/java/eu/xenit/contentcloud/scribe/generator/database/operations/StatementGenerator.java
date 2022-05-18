package eu.xenit.contentcloud.scribe.generator.database.operations;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.stream.Stream;

@FunctionalInterface
public interface StatementGenerator {
    Stream<Statement> generate(Operation operation);
}
