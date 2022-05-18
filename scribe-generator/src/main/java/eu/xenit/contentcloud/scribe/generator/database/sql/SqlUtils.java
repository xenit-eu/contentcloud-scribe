package eu.xenit.contentcloud.scribe.generator.database.sql;

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
        return table+"_"+column+"_idx";
    }

}
