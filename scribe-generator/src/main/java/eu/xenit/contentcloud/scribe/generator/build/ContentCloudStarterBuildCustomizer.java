package eu.xenit.contentcloud.scribe.generator.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

import java.util.NoSuchElementException;

public class ContentCloudStarterBuildCustomizer implements BuildCustomizer<Build> {

    private final InitializrMetadata metadata;

    private final BuildMetadataResolver buildMetadataResolver;

    public ContentCloudStarterBuildCustomizer(InitializrMetadata metadata) {
        this.metadata = metadata;
        this.buildMetadataResolver = new BuildMetadataResolver(metadata);
    }

    @Override
    public void customize(Build build) {
        if (!this.buildMetadataResolver.hasFacet(build, "contentcloud")) {

            // Import the contentcloud-starter, if no dependency contributed the facet 'contentcloud' yet
            Dependency dependency = determineContentCloudDependency(this.metadata);
            build.dependencies().add(dependency.getId(), MetadataBuildItemMapper.toDependency(dependency));
        }
    }

    private Dependency determineContentCloudDependency(InitializrMetadata metadata) {
        Dependency starter = metadata.getDependencies().get("contentcloud-starter");

        if (starter == null) {
            throw new NoSuchElementException("Dependency with id 'contentcloud-starter' not found");
        }

        return starter;
    }

}
