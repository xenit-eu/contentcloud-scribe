package eu.xenit.contentcloud.scribe.generator.entitymodel;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.source.SourceGenerator;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class EntityModelGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    EntityModel entityModel() {
        var changeSet = this.description.getChangeset();
        if (changeSet == null) {
            return new EntityModel(List.of());
        }

        return new EntityModel(changeSet.getEntities());
    }

    @Bean
    public EntityModelSourceCodeProjectContributor entityModelSourceCodeProjectContributor(EntityModel entityModel,
            SourceGenerator sourceGenerator) {
        return new EntityModelSourceCodeProjectContributor(this.description, entityModel, sourceGenerator);
    }

}
