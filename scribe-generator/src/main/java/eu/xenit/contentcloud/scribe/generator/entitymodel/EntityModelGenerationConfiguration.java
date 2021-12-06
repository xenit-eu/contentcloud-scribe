package eu.xenit.contentcloud.scribe.generator.entitymodel;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@ProjectGenerationConfiguration
public class EntityModelGenerationConfiguration {

    private final ScribeProjectDescription description;

    @Bean
    EntityModel entityModel() {
        return new EntityModel(this.description.getChangeset().getEntities());
    }

    @Bean
    public EntityModelSourceCodeProjectContributor entityModelSourceCodeProjectContributor(EntityModel entityModel) {
        return new EntityModelSourceCodeProjectContributor(this.description, entityModel);
    }

}
