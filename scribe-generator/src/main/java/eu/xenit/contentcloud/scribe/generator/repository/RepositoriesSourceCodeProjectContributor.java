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

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.entitymodel.EntityModel;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.SourceCodeWriter;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class RepositoriesSourceCodeProjectContributor implements ProjectContributor {

	private final ProjectDescription description;

	private final EntityModel entityModel;

	private final Supplier<JavaSourceCode> sourceFactory;

	private final SourceCodeWriter<JavaSourceCode> sourceWriter;

	@Override
	public void contribute(Path projectRoot) throws IOException {
		JavaSourceCode sourceCode = this.sourceFactory.get();

		this.entityModel.entities().forEachOrdered(entity -> {
			contributeJpaRepository(sourceCode, entity);
		});


		this.sourceWriter.writeTo(
				this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage()),
				sourceCode);
	}

	private void contributeJpaRepository(JavaSourceCode sourceCode, Entity entity) {
		var entityName = this.generateEntityClassName(entity.getName());
		var fullEntityName = this.description.getPackageName() + ".model." + entityName;

		var repositoryName = entityName + "Repository";

		var classFile = sourceCode.createCompilationUnit(this.description.getPackageName() + ".repository", repositoryName);

		var repositoryClass = classFile.createTypeDeclaration(repositoryName);
		repositoryClass.modifiers(Modifier.INTERFACE | Modifier.PUBLIC);
		repositoryClass.extend("org.springframework.data.jpa.repository.JpaRepository<" + fullEntityName + ", java.util.UUID>");

		repositoryClass.annotate(Annotation.name("org.springframework.data.rest.core.annotation.RepositoryRestResource"));
	}

	private String generateEntityClassName(String name) {
		String candidate = StringUtils.capitalize(name);
		if (hasInvalidChar(candidate) /* || check blacklist ? */) {
			throw new IllegalArgumentException("Invalid class name: "+name);
		}

		return candidate;
	}

	private static boolean hasInvalidChar(String text) {
		if (!Character.isJavaIdentifierStart(text.charAt(0))) {
			return true;
		}
		if (text.length() > 1) {
			for (int i = 1; i < text.length(); i++) {
				if (!Character.isJavaIdentifierPart(text.charAt(i))) {
					return true;
				}
			}
		}
		return false;
	}
}
