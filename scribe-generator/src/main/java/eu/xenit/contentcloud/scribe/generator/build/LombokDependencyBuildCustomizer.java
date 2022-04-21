package eu.xenit.contentcloud.scribe.generator.build;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LombokDependencyBuildCustomizer implements BuildCustomizer<Build> {

    private final ScribeProjectDescription description;
    private final InitializrMetadata metadata;

    @Override
    public void customize(Build build) {
        if (this.description.useLombok()) {
            var lombok = Optional.ofNullable(metadata.getDependencies().get("lombok")).orElseThrow();
            build.dependencies().add(lombok.getId(), MetadataBuildItemMapper.toDependency(lombok));
        }
    }
}
