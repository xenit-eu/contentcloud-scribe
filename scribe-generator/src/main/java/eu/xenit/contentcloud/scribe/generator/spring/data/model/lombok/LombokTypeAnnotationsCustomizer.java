package eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok;

public interface LombokTypeAnnotationsCustomizer {

    LombokTypeAnnotationsCustomizer useGetter(boolean useLombok);
    LombokTypeAnnotationsCustomizer useSetter(boolean useLombok);
    LombokTypeAnnotationsCustomizer useNoArgsConstructor(boolean useLombok);

}
