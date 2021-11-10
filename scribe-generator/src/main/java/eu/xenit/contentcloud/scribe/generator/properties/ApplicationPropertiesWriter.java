package eu.xenit.contentcloud.scribe.generator.properties;

import io.spring.initializr.generator.io.IndentingWriter;

public interface ApplicationPropertiesWriter {

    String getFormat();

    void writeTo(IndentingWriter writer, ApplicationProperties properties);
}
