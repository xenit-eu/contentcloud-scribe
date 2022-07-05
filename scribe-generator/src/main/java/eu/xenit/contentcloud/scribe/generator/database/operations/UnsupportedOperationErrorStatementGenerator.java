package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.sql.ErrorStatement.error;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UnsupportedOperationErrorStatementGenerator implements StatementGenerator {

    private final StatementGenerator generator;

    private final Set<String> skipOperations;

    public UnsupportedOperationErrorStatementGenerator(StatementGenerator generator, Set<String> skipOperations) {
        this.generator = generator;
        this.skipOperations = skipOperations;
    }

    public UnsupportedOperationErrorStatementGenerator(StatementGenerator generator, String ... skipOperations) {
        this.generator = generator;
        this.skipOperations = Set.of(skipOperations);
    }

    @Override
    public Stream<Statement> generate(Operation operation) {

        if (skipOperations.contains(operation.getType())) {
            return Stream.empty();
        }

        var data = generator.generate(operation).spliterator();
        var firstElement = new AtomicReference<Statement>();
        if (!data.tryAdvance(firstElement::set)) {
            return Stream.of(error(new UnsupportedOperationException("Unsupported operation "+operation)));
        }

        return Stream.concat(
                Stream.of(firstElement.get()),
                StreamSupport.stream(data, false)
        );
    }
}
