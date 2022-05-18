package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UnsupportedStatement implements Statement {
    private final RuntimeException ex;

    @Override
    public String toSql() {
        throw new RuntimeException("Unsupported statement", ex);
    }

    public static UnsupportedStatement unsupported(String message) {
        try {
            // Throwing an exception to get the stacktrace
            throw new RuntimeException(message);
        } catch(RuntimeException ex) {
            return new UnsupportedStatement(ex);
        }
    }
}
