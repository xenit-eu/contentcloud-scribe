package eu.xenit.contentcloud.scribe.generator.database.sql;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RenameTableStatement implements Statement{
    String oldName;
    String newName;

    @Override
    public String toSql() {
        return "ALTER TABLE "+q(oldName)+" RENAME TO "+q(newName)+";";
    }
}
