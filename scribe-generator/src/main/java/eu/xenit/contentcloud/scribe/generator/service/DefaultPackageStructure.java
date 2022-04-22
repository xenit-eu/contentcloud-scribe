package eu.xenit.contentcloud.scribe.generator.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultPackageStructure implements PackageStructure {

    @NonNull
    private final String packageName;

    public String getRepositoriesPackageName() {
        return this.packageName + ".repository";
    }

    public String getModelPackageName() {
        return this.packageName + ".model";
    }
}
