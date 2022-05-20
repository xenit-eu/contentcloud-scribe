package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.Builder;
import lombok.Value;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;
@Value
@Builder
public class CreateTableStatement implements Statement{
    String table;

    @Override
    public String toSql() {
        return "CREATE TABLE "+q(table)+" (id UUID PRIMARY KEY);";
    }
}
