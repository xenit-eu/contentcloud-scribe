package eu.xenit.contentcloud.scribe.generator.build.gradle;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

import java.util.NoSuchElementException;

public class ContentCloudAnnotationsStarterGradleBuildCustomizer implements BuildCustomizer<GradleBuild> {

    private final InitializrMetadata metadata;

    private final BuildMetadataResolver buildMetadataResolver;

    public ContentCloudAnnotationsStarterGradleBuildCustomizer(InitializrMetadata metadata) {
        this.metadata = metadata;
        this.buildMetadataResolver = new BuildMetadataResolver(metadata);
    }

    @Override
    public void customize(GradleBuild build) {
        if (build.dependencies().has("contentcloud-starter")) {
            Dependency dependency = determineContentCloudAnnotationsStarterDependency(this.metadata);
            build.dependencies().add(dependency.getId(), MetadataBuildItemMapper.toDependency(dependency));
        }
    }

    private Dependency determineContentCloudAnnotationsStarterDependency(InitializrMetadata metadata) {
        Dependency starter = metadata.getDependencies().get("contentcloud-annotations-starter");

        if (starter == null) {
            throw new NoSuchElementException("Dependency with id 'contentcloud-annotations-starter' not found");
        }

        return starter;
    }

}
