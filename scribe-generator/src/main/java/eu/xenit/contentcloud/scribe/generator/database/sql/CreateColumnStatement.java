package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;

@Value
@Builder
public class CreateColumnStatement implements Statement {
    @NonNull
    String table;
    @NonNull
    String column;
    @NonNull
    String dataType;
    @NonNull
    boolean nullable;

    @Override
    public String toSql() {
        return "ALTER TABLE "+q(table)+" ADD COLUMN "+q(column)+" "+dataType+" "+(nullable?"NULL":"NOT NULL")+";";
    }
}
