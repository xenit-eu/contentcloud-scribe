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
        var generator = new UnsupportedOperationErrorStatementGenerator(operation -> Stream.empty());
        var operation = new Operation("test-123", Collections.emptyMap(), Model.builder().build(), Model.builder().build());

        var statements = generator.generate(operation);

        assertThat(statements).hasAtLeastOneElementOfType(ErrorStatement.class);
    }

    @Test
    void doesNotAddStatementIfSomeGenerated() {
        var generator = new UnsupportedOperationErrorStatementGenerator(operation -> Stream.of(comment("Something")));
        var operation = new Operation("test-123", Collections.emptyMap(), Model.builder().build(), Model.builder().build());

        var statements = generator.generate(operation);

        assertThat(statements).containsExactly(comment("Something"));
    }

    @Test
    void skipsStatementIfInSkipList() {
        var generator = new UnsupportedOperationErrorStatementGenerator(operation -> Stream.of(comment("Something")), "skip-this");
        var operation = new Operation("skip-this", Collections.emptyMap(), Model.builder().build(), Model.builder().build());

        var statements = generator.generate(operation);
        assertThat(statements).isEmpty();
    }

}