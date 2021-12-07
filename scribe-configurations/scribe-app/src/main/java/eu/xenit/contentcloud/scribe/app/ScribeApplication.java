package eu.xenit.contentcloud.scribe.app;

import eu.xenit.contentcloud.scribe.changeset.ChangesetResolver;
import eu.xenit.contentcloud.scribe.drivers.rest.ScribeProjectRequestToDescriptionConverter;
import eu.xenit.contentcloud.scribe.drivers.rest.ScribeRestController;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.ChangesetRepository;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.ChangesetRepositoryProperties;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class ScribeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScribeApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(prefix = "scribe")
    ChangesetRepositoryProperties changeSetRepositoryProperties() {
        return new ChangesetRepositoryProperties();
    }

    @Bean
    ChangesetResolver changeSetResolver(ChangesetRepositoryProperties properties, RestTemplateBuilder restTemplateBuilder) {
        return new ChangesetRepository(properties, restTemplateBuilder.build());
    }

    @Bean
    RestTemplateCustomizer hypermediaRestTemplateCustomizer(HypermediaRestTemplateConfigurer configurer) {
        return restTemplate -> configurer.registerHypermediaTypes(restTemplate);
    }

    @Bean
    ScribeRestController projectGenerationController(
            InitializrMetadataProvider metadataProvider, ApplicationContext applicationContext,
            ChangesetResolver changeSetResolver) {
        var converter = new ScribeProjectRequestToDescriptionConverter(changeSetResolver);
        var invoker = new ProjectGenerationInvoker<>(applicationContext, converter);
        return new ScribeRestController(metadataProvider, invoker);
    }

    @Bean
    IndentingWriterFactory indentingWriterFactory() {
        return IndentingWriterFactory.create(new SimpleIndentStrategy("\t"), factory -> {
            // yml indentation is 2 or 4 spaces
            factory.indentingStrategy("yml", new SimpleIndentStrategy("  "));
        });
    }
}
