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

package eu.xenit.contentcloud.scribe.generator.entitymodel;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.SourceCodeWriter;
import io.spring.initializr.generator.language.java.JavaFieldDeclaration;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.GenerationType;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * {@link ProjectContributor} for the entity model source code
 */
@RequiredArgsConstructor
public class EntityModelSourceCodeProjectContributor implements ProjectContributor {

	private final ProjectDescription description;

	private final EntityModel entityModel;

	private final Supplier<JavaSourceCode> sourceFactory;

	private final SourceCodeWriter<JavaSourceCode> sourceWriter;

	@Override
	public void contribute(Path projectRoot) throws IOException {
		JavaSourceCode sourceCode = this.sourceFactory.get();
		String applicationName = this.description.getApplicationName();

		this.entityModel.entities().forEachOrdered(entity -> {
			contributeEntity(sourceCode, entity);
		});


		this.sourceWriter.writeTo(
				this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage()),
				sourceCode);
	}

	private void contributeEntity(JavaSourceCode sourceCode, Entity entity) {
		var name = this.generateEntityClassName(entity.getName());
		var classFile = sourceCode.createCompilationUnit(this.description.getPackageName() + ".model", name);
		var entityType = classFile.createTypeDeclaration(name);
		entityType.modifiers(Modifier.PUBLIC);

		entityType.annotate(Annotation.name("javax.persistence.Entity"));

		entityType.annotate(Annotation.name("lombok.Getter"));
		entityType.annotate(Annotation.name("lombok.Setter"));
		entityType.annotate(Annotation.name("lombok.NoArgsConstructor"));


		var idField = JavaFieldDeclaration.field("_id")
				.modifiers(Modifier.PRIVATE)
				.returning("java.util.UUID");
		idField.annotate(Annotation.name("javax.persistence.Id"));
		idField.annotate(Annotation.name("javax.persistence.GeneratedValue",
				an -> an.attribute("strategy", GenerationType.class, "javax.persistence.GenerationType.AUTO" )));
		entityType.addFieldDeclaration(idField);

		entity.getAttributes().forEach(attribute -> {
			var resolvedAttributeType = this.resolveAttributeType(attribute.getType());
			var field = JavaFieldDeclaration.field(attribute.getName())
					.modifiers(Modifier.PRIVATE)
					.returning(resolvedAttributeType);
			entityType.addFieldDeclaration(field);
			// add fields
		});
	}

	private String resolveAttributeType(String type) {
		if (Objects.equals(type, "String") || Objects.equals(type, "STRING")) {
			return String.class.getName();
		}

		if (Objects.equals(type, "DATETIME")) {
			return Instant.class.getName();
		}

		throw new IllegalArgumentException("cannot resolve data type: "+type);
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
