package eu.xenit.contentcloud.scribe.changeset;

import lombok.Data;

import java.util.Map;

@Data
public class Operation {

    private String type;
    private Map<String,Object> properties;
}
