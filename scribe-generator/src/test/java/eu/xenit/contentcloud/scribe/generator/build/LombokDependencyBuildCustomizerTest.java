package eu.xenit.contentcloud.scribe.generator.build;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.Test;

class LombokDependencyBuildCustomizerTest {

    @Test
    void lombokDependencyAdded_whenRequested() {
        var lombok = Dependency.withId("lombok",  "org.projectlombok", "lombok", null, Dependency.SCOPE_ANNOTATION_PROCESSOR);
        var metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("group", lombok).build();

        var description = new ScribeProjectDescription();
        description.useLombok(true);
        var build = createBuild(metadata);

        assertThat(build.dependencies().ids()).doesNotContain("lombok");
        new LombokDependencyBuildCustomizer(description, metadata).customize(build);
        assertThat(build.dependencies().ids()).containsOnly("lombok");
    }

    @Test
    void noLombokDependencyAdded_whenNotRequested() {
        var lombok = Dependency.withId("lombok",  "org.projectlombok", "lombok", null, Dependency.SCOPE_ANNOTATION_PROCESSOR);
        var metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("group", lombok).build();

        var description = new ScribeProjectDescription();
        description.useLombok(false);
        var build = createBuild(metadata);

        new LombokDependencyBuildCustomizer(description, metadata).customize(build);
        assertThat(build.dependencies().ids()).doesNotContain("lombok");
    }

    @Test
    void throws_whenMetadataIsNotAvailable() {
        var metadata = InitializrMetadataTestBuilder.withDefaults().build();

        var description = new ScribeProjectDescription();
        description.useLombok(true);

        var build = createBuild(metadata);

        assertThatThrownBy(() -> new LombokDependencyBuildCustomizer(description, metadata).customize(build));
    }

    private Build createBuild(InitializrMetadata metadata) {
        return new GradleBuild(new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
    }

}