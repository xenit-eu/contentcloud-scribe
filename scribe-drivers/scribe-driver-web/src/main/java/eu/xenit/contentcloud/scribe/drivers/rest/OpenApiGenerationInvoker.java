package eu.xenit.contentcloud.scribe.drivers.rest;

import eu.xenit.contentcloud.scribe.generator.openapi.OpenApiProjectContributor;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.project.ProjectAssetGenerator;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.project.ProjectGenerationException;
import io.spring.initializr.generator.project.ProjectGenerator;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import io.spring.initializr.web.project.MetadataProjectDescriptionCustomizer;
import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;
import java.io.StringWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OpenApiGenerationInvoker<R extends ScribeProjectRequest> {

    private final ApplicationContext parentApplicationContext;

    private final ApplicationEventPublisher eventPublisher;

    private final ProjectRequestToDescriptionConverter<R> requestConverter;

    public OpenApiGenerationInvoker(ApplicationContext parentApplicationContext,
            ProjectRequestToDescriptionConverter<R> requestConverter) {
        this(parentApplicationContext, parentApplicationContext, requestConverter);
    }

    public OpenApiGenerationInvoker(ApplicationContext parentApplicationContext,
            ApplicationEventPublisher eventPublisher,
            ProjectRequestToDescriptionConverter<R> requestConverter) {

        this.parentApplicationContext = parentApplicationContext;
        this.eventPublisher = eventPublisher;
        this.requestConverter = requestConverter;
    }


    /**
     * Invokes the project generation API that knows how to just write the OpenAPI spec.
     *
     * {@link ScribeProjectRequest}.
     *
     * @param request the project request
     * @return the generated OpenAPI spec contents
     */
    public byte[] invokeOpenapiGeneration(R request) {
        InitializrMetadata metadata = this.parentApplicationContext.getBean(InitializrMetadataProvider.class).get();
        try {
            ProjectDescription description = this.requestConverter.convert(request, metadata);
            ProjectGenerator projectGenerator = new ProjectGenerator((
                    projectGenerationContext) -> customizeProjectGenerationContext(projectGenerationContext, metadata));
            return projectGenerator.generate(description, generateOpenApiSpec(request));
        } catch (ProjectGenerationException ex) {
            publishProjectFailedEvent(request, metadata, ex);
            throw ex;
        }
    }

    private void customizeProjectGenerationContext(AnnotationConfigApplicationContext context,
            InitializrMetadata metadata) {
        context.setParent(this.parentApplicationContext);
        context.registerBean(InitializrMetadata.class, () -> metadata);
        context.registerBean(BuildItemResolver.class, () -> new MetadataBuildItemResolver(metadata,
                context.getBean(ProjectDescription.class).getPlatformVersion()));
        context.registerBean(MetadataProjectDescriptionCustomizer.class,
                () -> new MetadataProjectDescriptionCustomizer(metadata));
    }

    private ProjectAssetGenerator<byte[]> generateOpenApiSpec(R request) {
        return (context) -> {
            byte[] result;
            StringWriter out = new StringWriter();

            var openApiWriter = context.getBeanProvider(OpenApiProjectContributor.class).getIfAvailable();
            if (openApiWriter != null) {
                openApiWriter.writeOpenApiSpec(out);
                result = out.toString().getBytes();
            } else {
                throw new IllegalStateException(
                        "No %s found".formatted(OpenApiProjectContributor.class.getSimpleName()));
            }

            publishProjectGeneratedEvent(request, context);
            return result;
        };
    }

    private void publishProjectGeneratedEvent(R request, ProjectGenerationContext context) {
        InitializrMetadata metadata = context.getBean(InitializrMetadata.class);
        ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, metadata);
        this.eventPublisher.publishEvent(event);
    }

    private void publishProjectFailedEvent(R request, InitializrMetadata metadata, Exception cause) {
        ProjectFailedEvent event = new ProjectFailedEvent(request, metadata, cause);
        this.eventPublisher.publishEvent(event);
    }
}
