package eu.xenit.contentcloud.scribe.generator.database.operations;

import static eu.xenit.contentcloud.scribe.generator.database.sql.CommentStatement.comment;
import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.contentcloud.scribe.changeset.Model;
import eu.xenit.contentcloud.scribe.changeset.Operation;
import eu.xenit.contentcloud.scribe.generator.database.sql.Statement;
import eu.xenit.contentcloud.scribe.generator.database.sql.ErrorStatement;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class UnsupportedOperationStatementGeneratorTest {
    @Test
    void addsStatementIfNoneGenerated() {
        var generator = new UnsupportedOperationErrorStatementGenerator(new StatementGenerator() {
            @Override
            public Stream<Statement> generate(Operation operation) {
                return Stream.empty();
            }
        });

        var statements = generator.generate(new Operation("test-123", Collections.emptyMap(), Model.builder().build(), Model.builder().build()));

        assertThat(statements)
                .hasAtLeastOneElementOfType(ErrorStatement.class);
    }

    @Test
    void doesNotAddStatementIfSomeGenerated() {
        var generator = new UnsupportedOperationErrorStatementGenerator(new StatementGenerator() {
            @Override
            public Stream<Statement> generate(Operation operation) {
                return Stream.of(comment("Something"));
            }
        });

        var statements = generator.generate(new Operation("test-123", Collections.emptyMap(), Model.builder().build(), Model.builder().build()));

        assertThat(statements)
                .containsExactly(comment("Something"));
    }

}