package eu.xenit.contentcloud.scribe.app;

import eu.xenit.contentcloud.scribe.changeset.ChangeSetResolver;
import eu.xenit.contentcloud.scribe.drivers.rest.ScribeProjectRequestToDescriptionConverter;
import eu.xenit.contentcloud.scribe.drivers.rest.ScribeRestController;
import eu.xenit.contentcloud.scribe.infrastructure.changeset.ChangeSetRepository;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class ScribeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScribeApplication.class, args);
	}

	@Bean
	ChangeSetResolver changeSetResolver(RestTemplateBuilder restTemplateBuilder) {
		return new ChangeSetRepository(restTemplateBuilder.build());
	}

	@Bean
	public ScribeRestController projectGenerationController(
			InitializrMetadataProvider metadataProvider, ApplicationContext applicationContext,
			ChangeSetResolver changeSetResolver) {
		var converter = new ScribeProjectRequestToDescriptionConverter(changeSetResolver);
		var invoker = new ProjectGenerationInvoker<>(applicationContext, converter);
		return new ScribeRestController(metadataProvider, invoker);
	}
}
