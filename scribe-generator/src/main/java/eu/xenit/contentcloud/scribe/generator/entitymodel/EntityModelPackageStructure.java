package eu.xenit.contentcloud.scribe.generator.entitymodel;

import io.spring.initializr.generator.project.ProjectDescription;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityModelPackageStructure {

    protected final ProjectDescription description;

    public String getModelPackageName() {
        return this.description.getPackageName() + ".model";
    }
}
