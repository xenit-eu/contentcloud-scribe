package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ErrorStatement implements Statement {
    private final RuntimeException ex;

    @Override
    public String toSql() {
        throw new RuntimeException(ex);
    }

    public static ErrorStatement error(RuntimeException exception) {
        return new ErrorStatement(exception);
    }

    public static ErrorStatement error(String message) {
        try {
            // Throwing an exception to get the stacktrace
            throw new RuntimeException(message);
        } catch(RuntimeException ex) {
            return new ErrorStatement(ex);
        }
    }
}
