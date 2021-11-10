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

package eu.xenit.contentcloud.scribe.generator.repository;

import eu.xenit.contentcloud.scribe.poet.ClassName;
import eu.xenit.contentcloud.scribe.poet.JavaFile;
import eu.xenit.contentcloud.scribe.poet.ParameterizedTypeName;
import eu.xenit.contentcloud.scribe.poet.TypeSpec;
import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.entitymodel.EntityModel;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.SourceCodeWriter;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class RepositoriesPoetSourceCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;

    private final EntityModel entityModel;

    @Override
    public void contribute(Path projectRoot) throws IOException {
        SourceStructure mainSource = this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage());
        RepositoryPackageStructure packages = new RepositoryPackageStructure(this.description);

        for (Entity entity : this.entityModel.entities()) {
            contributeJpaRepository(mainSource, packages, entity);
        }
    }

    private void contributeJpaRepository(SourceStructure structure, RepositoryPackageStructure packages, Entity entity) throws IOException {

        var typeBuilder = TypeSpec.interfaceBuilder(entity.getClassName() + "Repository");
        var jpaRepoType = ParameterizedTypeName.get(
                ClassName.get("org.springframework.data.jpa.repository", "JpaRepository"),
                ClassName.get(packages.getModelPackageName(), entity.getClassName()),
                ClassName.get(UUID.class)
        );

        typeBuilder.addSuperinterface(jpaRepoType);
        typeBuilder.addAnnotation(ClassName.get("org.springframework.data.rest.core.annotation", "RepositoryRestResource"));

        // customize repositories here

        var javaFile = JavaFile.builder(packages.getRepositoriesPackageName(), typeBuilder.build())
                .indent("\t")
                .build();

        javaFile.writeTo(structure.getSourcesDirectory());
    }
}
