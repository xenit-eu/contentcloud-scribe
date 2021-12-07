package eu.xenit.contentcloud.scribe.generator.service;

import io.spring.initializr.generator.project.ProjectDescription;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultPackageStructure implements PackageStructure {

    private final ProjectDescription description;

    public String getRepositoriesPackageName() {
        return this.description.getPackageName() + ".repository";
    }

    public String getModelPackageName() {
        return this.description.getPackageName() + ".model";
    }
}
