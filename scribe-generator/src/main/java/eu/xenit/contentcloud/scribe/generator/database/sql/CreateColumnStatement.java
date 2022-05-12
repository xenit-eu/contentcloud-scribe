package eu.xenit.contentcloud.scribe.generator.database.sql;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

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
        return "ALTER TABLE "+table+" ADD COLUMN "+column+" "+dataType+" "+(nullable?"NULL":"NOT NULL")+";";
    }
}
