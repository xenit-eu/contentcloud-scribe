package eu.xenit.contentcloud.scribe.generator.database.operations;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AggregateStatementGenerator implements StatementGenerator{
    private final Set<StatementGenerator> generators;

    @Override
    public Stream<Statement> generate(Operation operation) {
        return generators.stream()
                .flatMap(generator -> generator.generate(operation));
    }
}
