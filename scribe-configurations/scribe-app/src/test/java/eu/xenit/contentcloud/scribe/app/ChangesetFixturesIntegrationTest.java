package eu.xenit.contentcloud.scribe.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.database.DatabaseMigrationProjectGenerationConfiguration;
import eu.xenit.contentcloud.scribe.generator.spring.content.SpringContentProjectionGenerationConfiguration;
import eu.xenit.contentcloud.scribe.generator.spring.data.SpringDataProjectGenerationConfiguration;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.ChangesetFactory;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ChangesetDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.dto.ProjectDto;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.model.Model;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.code.java.JavaProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;

@SpringBootTest
public class ChangesetFixturesIntegrationTest {

    private ProjectAssetTester projectTester;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectAssetTester()
                .withIndentingWriterFactory()
                .withConfiguration(
                        SpringDataProjectGenerationConfiguration.class,
                        SpringContentProjectionGenerationConfiguration.class,
                        JavaProjectGenerationConfiguration.class,
                        DatabaseMigrationProjectGenerationConfiguration.class
                )
                .withDirectory(directory)
                .withDescriptionCustomizer((description) -> {
                    description.setLanguage(new JavaLanguage());
                    description.setPlatformVersion(Version.parse("2.6.6"));
                    description.setBuildSystem(new GradleBuildSystem());
                });
    }

    @ParameterizedTest
    @ArgumentsSource(ChangesetTestFixturesProvider.class)
    void testChangesetFixture(URL changesetUrl) {
        var description = new ScribeProjectDescription();
        description.setChangeset(parseChangeset(changesetUrl));

        ProjectStructure project = this.projectTester.generate(description);
        project.assertThat().isNotNull();
    }

    @SneakyThrows
    private Changeset parseChangeset(URL changesetUrl) {
        var model = objectMapper.readValue(changesetUrl, new TypeReference<EntityModel<ChangesetDto>>() {});
        var changesetFactory = new ChangesetFactory(
                (changesetDto, contentType) -> new Model(objectMapper, changesetDto.getBaseModel()));
        return changesetFactory.create(model.getContent(), new ProjectDto("project", "org", "org/project"), MediaType.APPLICATION_JSON);
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

}
