package eu.xenit.contentcloud.scribe.generator.database.operations;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.CommentStatement;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentFilteringStatementGenerator implements StatementGenerator {
    private StatementGenerator wrapped;


    @Override
    public Stream<Statement> generate(Operation operation) {
        return wrapped.generate(operation)
                .filter(statement -> !(statement instanceof CommentStatement));
    }
}
