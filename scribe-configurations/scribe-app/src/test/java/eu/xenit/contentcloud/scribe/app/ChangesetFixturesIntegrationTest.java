package eu.xenit.contentcloud.scribe.app;

import static io.spring.initializr.metadata.Dependency.SCOPE_ANNOTATION_PROCESSOR;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.data.SpringDataProjectGenerationConfiguration;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.ChangesetModel;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.code.java.JavaProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.hateoas.EntityModel;

@SpringBootTest
public class ChangesetFixturesIntegrationTest {

    private ProjectAssetTester projectTester;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
                .withConfiguration(
                        SpringDataProjectGenerationConfiguration.class,
                        JavaProjectGenerationConfiguration.class)
                .withDirectory(directory)
                .withDescriptionCustomizer((description) -> {
                    description.setLanguage(new JavaLanguage());
                    if (description.getPlatformVersion() == null) {
                        description.setPlatformVersion(Version.parse("2.6.6"));
                    }
                    description.setBuildSystem(new GradleBuildSystem());
                });
    }

    static class ChangesetTestFixturesProvider implements ArgumentsProvider {

        private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            var resources = resolver.getResources("classpath*:fixtures/changesets/*.json");
            return Stream.of(resources).map(wrap(Resource::getURL))
                    .map(Arguments::of);
        }

        private static <T,R> Function<T, R> wrap(IOThrowingFunction<T, R> function) {
            return arg -> {
                try {
                    return function.apply(arg);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };
        }

        private interface IOThrowingFunction<T,R> {
            R apply(T t) throws IOException;
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ChangesetTestFixturesProvider.class)
    void testChangesetFixtures(URL changesetUrl) {

        var description = createProjectDescription();
        Changeset changeset = parseChangeset(changesetUrl);
        description.setChangeset(changeset);

        ProjectStructure project = this.projectTester.generate(description);
        project.assertThat().isNotNull();
    }

    @SneakyThrows
    private Changeset parseChangeset(URL changesetUrl) {
        var model = objectMapper.readValue(changesetUrl, new TypeReference<EntityModel<ChangesetModel>>() {});

        return Changeset.builder()
                .project("project")
                .organization("org")
                .entities(model.getContent().getEntities())
                .operations(model.getContent().getOperations())
                .build();
    }

    @Test
    void janBreaksScribe() throws IOException {

        var changesetLink = new ClassPathResource(
                "fixtures/changesets/0076b3b6-69ea-483d-9ebf-8a2b56adc4ca.json").getURL();
        var typeReference = new TypeReference<EntityModel<ChangesetModel>>() {
        };
        var changesetModel = objectMapper.readValue(changesetLink, typeReference);

        var changeset = Changeset.builder()
                .project("my-project")
                .organization("jan")
                .entities(changesetModel.getContent().getEntities())
                .operations(changesetModel.getContent().getOperations())
                .build();

        var description = createProjectDescription();
        description.setChangeset(changeset);

        ProjectStructure project = this.projectTester.generate(description);
        project.assertThat().isNotNull();
    }

    private static ScribeProjectDescription createProjectDescription() {
        var description = new ScribeProjectDescription();

        var lombok = Dependency.withId("lombok", "org.projectlombok", "lombok", null, SCOPE_ANNOTATION_PROCESSOR);
        description.addDependency(lombok.getId(), MetadataBuildItemMapper.toDependency(lombok));
        description.useLombok(true);

        return description;
    }

}
