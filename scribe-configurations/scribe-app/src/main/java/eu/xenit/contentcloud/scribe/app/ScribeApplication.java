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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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

    /**
     * If the current {@link Authentication} contains {@link AbstractOAuth2Token} credentials, propagate the
     * request's bearer token with a {@link RestTemplate} {@link AbstractOAuth2Token}
     *
     * @return the {@link RestTemplateCustomizer} that propagates the bearer token from the current request
     *
     * @see <a href="https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/bearer-tokens.html#_resttemplate_support">Spring Security - OAuth 2.0 Bearer Tokens</a>
     */
    @Bean
    RestTemplateCustomizer bearerTokenPropagationRestTemplateCustomizer() {
        return restTemplate -> restTemplate.getInterceptors().add((request, body, execution) -> {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null) {
                    return execution.execute(request, body);
                }

                if ((authentication.getCredentials() instanceof AbstractOAuth2Token)) {
                    AbstractOAuth2Token token = (AbstractOAuth2Token) authentication.getCredentials();
                    request.getHeaders().setBearerAuth(token.getTokenValue());
                }

                return execution.execute(request, body);
        });
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
