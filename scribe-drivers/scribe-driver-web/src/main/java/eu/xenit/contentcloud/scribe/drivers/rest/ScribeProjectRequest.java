package eu.xenit.contentcloud.scribe.drivers.rest;

import io.spring.initializr.web.project.WebProjectRequest;
import lombok.Getter;
import lombok.Setter;

public class ScribeProjectRequest extends WebProjectRequest {

    @Getter @Setter
    private String changeset;

    @Getter @Setter
    private boolean lombok = true;

}
