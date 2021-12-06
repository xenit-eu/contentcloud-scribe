package eu.xenit.contentcloud.scribe.generator.service;

import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class ApiServiceGenerationConfiguration {

    @Bean
    ApplicationPropertiesCustomizer serverPortCustomizer() {
        return properties -> properties.put("server.port", "${PORT:8080}");
    }
}
