package eu.xenit.contentcloud.scribe.generator.spring.content;

import eu.xenit.contentcloud.scribe.generator.properties.ApplicationPropertiesCustomizer;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import org.springframework.context.annotation.Bean;

@ProjectGenerationConfiguration
public class SpringContentProjectionGenerationConfiguration {

    @Bean
    ApplicationPropertiesCustomizer springContentDefaultStorage() {
        return properties -> properties.put("spring.content.storage.type.default", "fs");
    }
}
