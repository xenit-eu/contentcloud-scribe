package eu.xenit.contentcloud.scribe.generator.database.sql;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DropTableStatement implements Statement{
    String table;

    @Override
    public String toSql() {
        return "DROP TABLE "+q(table)+";";
    }
}
