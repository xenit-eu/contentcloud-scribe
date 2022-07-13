package eu.xenit.contentcloud.scribe.generator.swaggerui;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SwaggerUIDependencyBuildCustomizer implements BuildCustomizer<Build> {

    private final ScribeProjectDescription description;
    private final InitializrMetadata metadata;

    @Override
    public void customize(Build build) {
        if (this.description.enableSwaggerUI()) {
            var swaggerUI = Optional.ofNullable(metadata.getDependencies().get("swagger-ui")).orElseThrow();
            build.dependencies().add(swaggerUI.getId(), MetadataBuildItemMapper.toDependency(swaggerUI));
            var webjarsLocator = Optional.ofNullable(metadata.getDependencies().get("webjars-locator-core")).orElseThrow();
            build.dependencies().add(webjarsLocator.getId(), MetadataBuildItemMapper.toDependency(webjarsLocator));
        }
    }
}
