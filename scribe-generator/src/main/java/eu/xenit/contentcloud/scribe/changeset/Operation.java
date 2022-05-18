package eu.xenit.contentcloud.scribe.changeset;

import lombok.AllArgsConstructor;

import java.util.Map;
import lombok.ToString;
import lombok.Value;

@Value
@AllArgsConstructor
@ToString
public class Operation {

    private String type;
    private Map<String,Object> properties;

    private Model beforeModel;
    private Model afterModel;

    public Object getProperty(String name) {
        return properties.get(name);
    }
}
