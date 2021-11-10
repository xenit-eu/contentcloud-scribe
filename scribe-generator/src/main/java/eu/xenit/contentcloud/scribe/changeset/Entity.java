package eu.xenit.contentcloud.scribe.changeset;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Data
public class Entity {

    private String name;

    private List<Attribute> attributes;
    private List<Relation> relations;

    public String getClassName() {
        String candidate = StringUtils.capitalize(this.name);
        if (hasInvalidChar(candidate) /* || check blacklist ? */) {
            throw new IllegalArgumentException("Invalid class name: "+name);
        }

        return candidate;
    }

    public String getTableName() {
        return camelToSnake(this.getName());
    }

    private static boolean hasInvalidChar(String text) {
        if (!Character.isJavaIdentifierStart(text.charAt(0))) {
            return true;
        }
        if (text.length() > 1) {
            for (int i = 1; i < text.length(); i++) {
                if (!Character.isJavaIdentifierPart(text.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    static String camelToSnake(String name) {
        return name.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase(Locale.ROOT);
    }

}
