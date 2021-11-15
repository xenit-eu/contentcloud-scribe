package eu.xenit.contentcloud.scribe.generator;

import eu.xenit.contentcloud.scribe.changeset.ChangeSet;
import io.spring.initializr.generator.project.MutableProjectDescription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class ScribeProjectDescription extends MutableProjectDescription {

    @Getter @Setter
    private ChangeSet changeSet;

    @Getter @Setter @Accessors(fluent = true)
    private boolean useLombok;


}
