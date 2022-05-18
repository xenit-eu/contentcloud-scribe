package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.Builder;
import lombok.Value;
import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;
@Value
@Builder
public class RenameColumnStatement implements Statement {
    String table;
    String oldColumnName;
    String newColumnName;

    @Override
    public String toSql() {
        return "ALTER TABLE "+q(table)+" RENAME COLUMN "+q(oldColumnName)+" TO "+q(newColumnName)+";";
    }
}
