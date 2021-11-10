package eu.xenit.contentcloud.scribe.generator.repository;

import eu.xenit.contentcloud.scribe.generator.entitymodel.EntityModelPackageStructure;
import io.spring.initializr.generator.project.ProjectDescription;

public class RepositoryPackageStructure extends EntityModelPackageStructure {

    public RepositoryPackageStructure(ProjectDescription description) {
        super(description);
    }

    public String getRepositoriesPackageName() {
        return this.description.getPackageName() + ".repository";
    }
}
