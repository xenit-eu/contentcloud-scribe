package eu.xenit.contentcloud.scribe.generator.openapi;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import eu.xenit.contentcloud.scribe.generator.openapi.model.OpenApiModel;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class OpenApiYmlWriter {

    private final ObjectMapper om = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER))
            .setSerializationInclusion(Include.NON_NULL);

    public void writeOpenApiSpec(Writer writer, OpenApiModel model) throws IOException {
        try (PrintWriter out = new PrintWriter(writer)) {
            om.writeValue(out, model);
        }
    }
}