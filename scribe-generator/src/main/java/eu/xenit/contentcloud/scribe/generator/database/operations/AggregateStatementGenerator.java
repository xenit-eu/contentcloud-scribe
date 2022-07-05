package eu.xenit.contentcloud.scribe.generator.database.operations;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

public class AggregateStatementGenerator implements StatementGenerator {

    private final List<StatementGenerator> generators;

    public AggregateStatementGenerator(StatementGenerator ... generators) {
        this.generators = List.of(generators);
    }

    @Override
    public Stream<Statement> generate(Operation operation) {
        return generators.stream()
                .flatMap(generator -> generator.generate(operation));
    }
}
