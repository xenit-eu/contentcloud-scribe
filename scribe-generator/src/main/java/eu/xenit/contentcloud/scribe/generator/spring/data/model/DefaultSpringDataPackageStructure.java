package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultSpringDataPackageStructure implements SpringDataPackageStructure {

    @NonNull
    private final String packageName;

    public String getRepositoriesPackageName() {
        return this.packageName + ".repository";
    }

    public String getModelPackageName() {
        return this.packageName + ".model";
    }
}
