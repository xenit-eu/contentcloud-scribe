package eu.xenit.contentcloud.scribe.changeset;

import lombok.Data;

@Data
public class Attribute {

    private String name;
    private String type;

    private boolean indexed;
    private boolean naturalId;
    private boolean required;
    private boolean unique;

}
