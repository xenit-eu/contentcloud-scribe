package eu.xenit.contentcloud.scribe.generator.database.sql;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.springframework.lang.Nullable;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.index;
@Builder
@Value
public class CreateIndexStatement implements Statement {
    @Nullable
    @Getter(value = AccessLevel.NONE)
    String name;

    @NonNull
    String table;

    @NonNull
    @Singular
    List<String> columns;
    boolean unique;

    public static class CreateIndexStatementBuilder {
        public CreateIndexStatementBuilder forStatement(CreateColumnStatement createColumnStatement) {
            return table(createColumnStatement.getTable())
                    .column(createColumnStatement.getColumn());
        }
    }

    @NonNull
    public String getName() {
        if(name != null) {
            return name;
        }
        return index(table, columns);
    }

    @Override
    public String toSql() {
        String columns = this.columns.stream()
                .map(SqlUtils::q)
                .collect(Collectors.joining(", "));
        return "CREATE "+(unique?"UNIQUE INDEX":"INDEX")+" CONCURRENTLY "+q(getName())+" ON "+q(table)+"("+columns+");";
    }
}
