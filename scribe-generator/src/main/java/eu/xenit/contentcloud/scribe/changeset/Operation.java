package eu.xenit.contentcloud.scribe.changeset;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Operation {

    private String type;
    private Map<String,Object> properties;
}
