package eu.xenit.contentcloud.scribe.generator.database.sql;

import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.q;
import static eu.xenit.contentcloud.scribe.generator.database.sql.SqlUtils.index;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class RenameIndexStatement implements Statement {
    @NonNull
    String oldName;
    @NonNull
    String newName;

    public static RenameIndexStatement forColumn(RenameColumnStatement renameColumnStatement)  {
        return builder()
                .oldName(index(renameColumnStatement.getTable(), renameColumnStatement.getOldColumnName()))
                .newName(index(renameColumnStatement.getTable(), renameColumnStatement.getNewColumnName()))
                .build();
    }

    public static RenameIndexStatement forTable(RenameTableStatement renameTableStatement, String columnName) {
        return builder()
                .oldName(index(renameTableStatement.getOldName(), columnName))
                .newName(index(renameTableStatement.getNewName(), columnName))
                .build();
    }

    @Override
    public String toSql() {
        return "ALTER INDEX "+q(oldName)+" RENAME TO "+q(newName)+";";
    }
}
