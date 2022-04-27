/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.xenit.contentcloud.scribe.generator.spring.data;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SourceCodeGenerator;
import eu.xenit.contentcloud.scribe.generator.spring.data.source.SourceFile;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaRepository;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class SpringDataRepositorySourceCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;

    private final EntityModel entityModel;

    private final SourceCodeGenerator sourceGenerator;

    @Override
    public void contribute(Path projectRoot) throws IOException {
        SourceStructure mainSource = this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage());

        for (Entity entity : this.entityModel.entities()) {
            var sourceFile = contributeJpaRepository(entity);
            sourceFile.writeTo(mainSource.getSourcesDirectory());
        }
    }

    private SourceFile contributeJpaRepository(Entity entity) {

        var repo = JpaRepository.forEntity(entity.getClassName());

        return this.sourceGenerator.createSourceFile(repo);
    }
}
