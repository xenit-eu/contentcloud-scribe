package eu.xenit.contentcloud.scribe.generator.build;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.build.gradle.ContentCloudAnnotationsStarterGradleBuildCustomizer;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.metadata.InitializrMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class ContentCloudBuildProjectGenerationConfiguration {

    private final ScribeProjectDescription projectDescription;

    @Bean
    public ContentCloudStarterBuildCustomizer contentCloudStarterBuildCustomizer(InitializrMetadata metadata) {
        return new ContentCloudStarterBuildCustomizer(metadata);
    }

    @Bean
    @ConditionalOnBuildSystem(GradleBuildSystem.ID)
    public ContentCloudAnnotationsStarterGradleBuildCustomizer contentCloudAnnotationsStarterGradleBuildCustomizer(
            InitializrMetadata metadata) {
        return new ContentCloudAnnotationsStarterGradleBuildCustomizer(metadata);
    }

    @Bean
    public LombokDependencyBuildCustomizer lombokDependencyBuildCustomizer(InitializrMetadata metadata) {
        return new LombokDependencyBuildCustomizer(this.projectDescription, metadata);
    }

}
