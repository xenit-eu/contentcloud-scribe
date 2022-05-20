package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.sql.ErrorStatement.error;

import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnsupportedOperationErrorStatementGenerator implements StatementGenerator {
    private final StatementGenerator generator;

    @Override
    public Stream<Statement> generate(Operation operation) {
        var data = generator.generate(operation).spliterator();
        var firstElement = new AtomicReference<Statement>();
        if(!data.tryAdvance(firstElement::set)) {
            return Stream.of(error(new UnsupportedOperationException("Unsupported operation "+operation)));
        }

        return Stream.concat(
                Stream.of(firstElement.get()),
                StreamSupport.stream(data, false)
        );
    }
}
