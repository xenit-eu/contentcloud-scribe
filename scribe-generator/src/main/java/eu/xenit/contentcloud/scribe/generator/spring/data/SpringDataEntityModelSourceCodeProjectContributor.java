package eu.xenit.contentcloud.scribe.generator.spring.data;

import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JpaEntityModel;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityFactory;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SpringDataSourceCodeGenerator;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class SpringDataEntityModelSourceCodeProjectContributor implements ProjectContributor {

    private final ScribeProjectDescription description;

    private final JpaEntityModel entityModel;

    private final SpringDataSourceCodeGenerator sourceGenerator;

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Language language = this.description.getLanguage();

        SourceStructure mainSource = this.description.getBuildSystem().getMainSource(projectRoot, language);

        for (JpaEntity jpaEntity : this.entityModel.entities()) {
            var source = this.sourceGenerator.createSourceFile(jpaEntity);
            source.writeTo(mainSource.getSourcesDirectory());
        }
    }

}
