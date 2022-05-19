package eu.xenit.contentcloud.scribe.generator.database.sql;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import lombok.experimental.UtilityClass;

@UtilityClass
class SqlUtils {
    public static String q(String name) {
        if(name.contains("\"")) {
            throw new IllegalArgumentException("String containing \" can not safely be quoted.");
        }
        return "\""+name+"\"";
    }

    public static String index(String table, String column) {
        return index(table, Collections.singletonList(column));
    }

    public static String index(String table, List<String> column) {
        return concat(table, concat(column))+"_idx";
    }

    public static String concat(List<String> items ) {
        return concat(items.toArray(new String[0]));
    }

    public static String concat(String... items) {
        var joiner = new StringJoiner("__");
        for (String item : items) {
            joiner.add(item);
        }
        return joiner.toString();
    }
}
