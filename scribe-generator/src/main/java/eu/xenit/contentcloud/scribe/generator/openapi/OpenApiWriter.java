package eu.xenit.contentcloud.scribe.generator.openapi;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import java.io.BufferedWriter;
import java.io.IOException;

public class OpenApiWriter {

    public void writeOpenApiSpec(BufferedWriter writer, EntityModel entityModel) throws IOException {
        for (Entity entity : entityModel.entities()) {
            writer.append("hello from "+entity.getName());
        }
    }
}
