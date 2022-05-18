package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.index;
@Builder
@Value
public class CreateIndexStatement implements Statement {
    @NonNull
    String table;
    @NonNull
    String column;
    @NonNull
    boolean unique;

    public static class CreateIndexStatementBuilder {
        public CreateIndexStatementBuilder forStatement(CreateColumnStatement createColumnStatement) {
            return table(createColumnStatement.getTable())
                    .column(createColumnStatement.getColumn());
        }
    }

    @Override
    public String toSql() {
        return "CREATE "+(unique?"UNIQUE INDEX":"INDEX")+" CONCURRENTLY "+q(index(table, column))+" ON "+q(table)+"("+q(column)+");";
    }
}
