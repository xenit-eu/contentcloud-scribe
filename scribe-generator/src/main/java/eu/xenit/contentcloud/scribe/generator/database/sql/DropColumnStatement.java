package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.Builder;
import lombok.Value;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;

@Value
@Builder
public class DropColumnStatement implements Statement {
    String table, column;

    @Override
    public String toSql() {
        return "ALTER TABLE "+q(table)+ " DROP COLUMN "+q(column)+";";
    }
}
