package eu.xenit.contentcloud.scribe.generator;

import eu.xenit.contentcloud.scribe.changeset.ChangeSet;
import io.spring.initializr.generator.project.MutableProjectDescription;
import lombok.Getter;
import lombok.Setter;

public class ScribeProjectDescription extends MutableProjectDescription {

    @Getter @Setter
    private ChangeSet changeSet;
}
