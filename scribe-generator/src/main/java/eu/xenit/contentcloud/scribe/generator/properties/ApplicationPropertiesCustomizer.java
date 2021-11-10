package eu.xenit.contentcloud.scribe.generator.properties;

@FunctionalInterface
public interface ApplicationPropertiesCustomizer {

    void customize(ApplicationProperties properties);

}
