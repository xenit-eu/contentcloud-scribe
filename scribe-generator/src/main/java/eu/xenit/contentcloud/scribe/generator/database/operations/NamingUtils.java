package eu.xenit.contentcloud.scribe.generator.database.operations;

import lombok.experimental.UtilityClass;
import org.springframework.data.util.ParsingUtils;

@UtilityClass
public class NamingUtils {
    static String convertEntityNameToTableName(String name) {
        return "\""+ParsingUtils.reconcatenateCamelCase(name, "_")+"\"";
    }

    static String convertAttributeNameToColumnName(String name) {
        return "\""+ParsingUtils.reconcatenateCamelCase(name, "_")+"\"";
    }

}
