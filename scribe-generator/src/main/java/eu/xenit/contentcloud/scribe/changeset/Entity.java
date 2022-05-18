package eu.xenit.contentcloud.scribe.changeset;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import org.springframework.util.StringUtils;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Entity {

    private String name;

    @Singular
    private List<Attribute> attributes;

    @Singular
    private List<Relation> relations;

    public Optional<Attribute> getAttribute(String name) {
        return attributes
                .stream()
                .filter(attr -> Objects.equals(name, attr.getName()))
                .findFirst();
    }

    public Optional<Relation> getRelation(String name) {
        return relations
                .stream()
                .filter(attr -> Objects.equals(name, attr.getName()))
                .findFirst();
    }

    public String getClassName() {
        String candidate = StringUtils.capitalize(this.name);
        if (hasInvalidChar(candidate) /* || check blacklist ? */) {
            throw new IllegalArgumentException("Invalid class name: "+name);
        }

        return candidate;
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
