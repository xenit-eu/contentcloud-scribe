package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.lang.Nullable;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;

@Value
@Builder
public class CreateColumnStatement implements Statement {
    @NonNull
    String table;
    @NonNull
    String column;
    @NonNull
    DataType dataType;
    boolean nullable;

    @Nullable
    ForeignKeyReference foreignKey;

    @Override
    public String toSql() {
        String fkRef = (foreignKey == null?"": " REFERENCES "+q(foreignKey.table)+"("+q(foreignKey.column)+")");
        return "ALTER TABLE "+q(table)+" ADD COLUMN "+q(column)+" "+dataType.pgType+" "+(nullable?"NULL":"NOT NULL")+fkRef+";";
    }

    public static class CreateColumnStatementBuilder {
        public CreateColumnStatementBuilder foreignKey(ForeignKeyReference foreignKeyReference) {
            this.foreignKey = foreignKeyReference;
            return this;
        }

        public CreateColumnStatementBuilder foreignKey(String table, String column) {
            return foreignKey(
                    ForeignKeyReference.builder()
                            .table(table)
                            .column(column)
                            .build()
            );
        }
    }

    @AllArgsConstructor
    public enum DataType {
        TEXT("text"),
        BIGINT("bigint"),
        DATETIME("datetime"),
        UUID("uuid");

        private final String pgType;
    }

    @Value
    @Builder
    public static class ForeignKeyReference {
        @NonNull
        String table;
        @NonNull
        String column;
    }
}
