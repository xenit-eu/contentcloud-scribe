package eu.xenit.contentcloud.scribe.changeset;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Operation {

    private String type;
    private Map<String,Object> properties;

    public Object getProperty(String name) {
        return properties.get(name);
    }
}
