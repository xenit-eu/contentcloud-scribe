package eu.xenit.contentcloud.scribe.changeset;

import lombok.Data;

import java.util.List;

@Data
public class Entity {

    private String name;

    private List<Attribute> attributes;
    private List<Relation> relations;
}
