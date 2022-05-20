package eu.xenit.contentcloud.scribe.generator.database.sql;

import java.util.stream.Stream;
import lombok.Value;

@Value
public class CommentStatement implements Statement {
    String comment;

    @Override
    public String toSql() {
        return "-- "+ comment.replaceAll("\\n", "\n--");
    }

    public static CommentStatement comment(String comment) {
        return new CommentStatement(comment);
    }

    public Stream<Statement> wrap(Stream<Statement> statements) {
        return wrap(comment, statements);
    }

    public static Stream<Statement> wrap(String comment, Stream<Statement> statements) {
        return Stream.concat(
                Stream.of(comment("BEGIN: "+comment)),
                Stream.concat(
                        statements,
                        Stream.of(comment("END: "+comment))
                )
        );
    }
}
