package eu.xenit.contentcloud.scribe.generator.source;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.service.DefaultPackageStructure;
import eu.xenit.contentcloud.scribe.generator.service.PackageStructure;
import eu.xenit.contentcloud.scribe.generator.source.code.java.JavaSourceGenerator;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class SourceCodeProjectGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    PackageStructure packageStructure() {
        return new DefaultPackageStructure(this.description.getPackageName());
    }

    @Bean
    SourceGenerator sourceGenerator(PackageStructure packages) {
        Language language = this.description.getLanguage();
        if (language instanceof JavaLanguage) {
            return new JavaSourceGenerator((JavaLanguage) language, packages);
        }

        throw new UnsupportedOperationException(String.format("Language '%s' is not supported", language));
    }
}
