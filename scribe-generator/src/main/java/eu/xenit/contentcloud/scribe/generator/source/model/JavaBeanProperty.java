package eu.xenit.contentcloud.scribe.generator.source.model;

import eu.xenit.contentcloud.bard.TypeName;

public interface JavaBeanProperty {

    String name();
    JavaBeanProperty name(String name);

    TypeName type();
    JavaBeanProperty type(TypeName type);

}


