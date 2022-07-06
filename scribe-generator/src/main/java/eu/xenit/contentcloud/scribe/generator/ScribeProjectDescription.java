package eu.xenit.contentcloud.scribe.generator;

import eu.xenit.contentcloud.scribe.changeset.Changeset;
import io.spring.initializr.generator.project.MutableProjectDescription;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class ScribeProjectDescription extends MutableProjectDescription {

    @Getter @Setter
    private Changeset changeset;

    @Getter @Setter @Accessors(fluent = true)
    private boolean useLombok = true;

    @Getter @Setter @Accessors(fluent = true)
    private boolean enableSwaggerUI = true;


}
