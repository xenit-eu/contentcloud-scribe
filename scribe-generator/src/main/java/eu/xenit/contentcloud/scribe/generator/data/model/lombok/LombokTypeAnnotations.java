package eu.xenit.contentcloud.scribe.generator.data.model.lombok;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter
@Accessors(fluent = true, chain = true)
public class LombokTypeAnnotations implements LombokTypeAnnotationsConfig, LombokTypeAnnotationsCustomizer {

    private boolean useGetter = false;
    private boolean useSetter = false;
    private boolean useNoArgsConstructor = false;

}
