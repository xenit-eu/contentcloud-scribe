package eu.xenit.contentcloud.scribe.generator.spring.content;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.ContentStore;
import eu.xenit.contentcloud.scribe.generator.spring.content.source.SpringContentSourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.EntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaRepository;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringContentStoreSourceCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;

    private final EntityModel entityModel;

    private final SpringContentSourceCodeGenerator sourceGenerator;

    @Override
    public void contribute(Path projectRoot) throws IOException {
        SourceStructure mainSource = this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage());

        for (Entity entity : this.entityModel.entities()) {

            // does this entity have any 'Content' attributes ?
            if (entity.getAttributes().stream().anyMatch(attribute -> "CONTENT".equals(attribute.getType()))) {
                var sourceFile = contributeContentStore(entity);
                sourceFile.writeTo(mainSource.getSourcesDirectory());
            }
        }
    }

    private SourceFile contributeContentStore(Entity entity) {
        var contentStore = ContentStore.forEntity(entity.getClassName());
        return this.sourceGenerator.createSourceFile(contentStore);
    }
}
