package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsConfig;
import java.util.stream.Stream;

public interface JavaBean extends TypeDeclaration {

    Stream<? extends JavaBeanProperty> fields();

    LombokTypeAnnotationsConfig lombok();
}


