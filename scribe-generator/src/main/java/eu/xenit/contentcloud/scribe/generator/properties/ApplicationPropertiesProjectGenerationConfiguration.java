package eu.xenit.contentcloud.scribe.generator.properties;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class ApplicationPropertiesProjectGenerationConfiguration {

    private final ProjectDescription projectDescription;

    @Bean
    ProjectContributor removeApplicationProperties() {
        return new ProjectContributor() {
            @Override
            public void contribute(Path projectRoot) throws IOException {
                Files.delete(projectRoot.resolve("src/main/resources/application.properties"));
            }

            @Override
            public int getOrder() {
                return 5;
            }
        };
    }

    @Bean
    ApplicationProperties applicationProperties(List<ApplicationPropertiesCustomizer> propertiesCustomizers) {
        var properties = new ApplicationProperties();
        LambdaSafe.callbacks(ApplicationPropertiesCustomizer.class, propertiesCustomizers, properties)
                .invoke((customizer) -> customizer.customize(properties));
        return properties;
    }

    @Bean
    ApplicationPropertiesWriter applicationPropertiesWriter() {
        return new ApplicationYmlWriter();
    }

    @Bean
    ProjectContributor applicationPropertiesProjectContributor(ApplicationProperties properties,
                                          IndentingWriterFactory indentingWriterFactory,
                                          ApplicationPropertiesWriter propertiesWriter) {
        return new ProjectContributor() {

            @Override
            public void contribute(Path projectRoot) throws IOException {
                SourceStructure mainSource = projectDescription.getBuildSystem().getMainSource(projectRoot, projectDescription.getLanguage());
                Path resourcesDir = projectRoot.resolve(mainSource.getResourcesDirectory());
                Files.createDirectories(resourcesDir);
                Path yml = Files.createFile(resourcesDir.resolve("application.yml"));

                this.writeApplicationProperties(Files.newBufferedWriter(yml));
            }

            private void writeApplicationProperties(Writer out) throws IOException {

                try (IndentingWriter writer = indentingWriterFactory.createIndentingWriter(propertiesWriter.getFormat(), out)) {
                    propertiesWriter.writeTo(writer, properties);
                }
            }
        };
    }
}
