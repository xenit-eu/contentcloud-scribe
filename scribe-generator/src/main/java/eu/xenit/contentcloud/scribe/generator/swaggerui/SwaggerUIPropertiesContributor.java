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

package eu.xenit.contentcloud.scribe.generator.swaggerui;

import io.spring.initializr.generator.project.contributor.SingleResourceProjectContributor;

/**
 * A {@link SingleResourceProjectContributor} that contributes a
 * {@code application.properties} file to a project.
 *
 * @author Stephane Nicoll
 */
public class SwaggerUIPropertiesContributor extends SingleResourceProjectContributor {

    public SwaggerUIPropertiesContributor() {
        this("classpath:webjars/swagger-ui/4.11.1/swagger-initializer.js");
    }

    public SwaggerUIPropertiesContributor(String resourcePattern) {
        super("src/main/resources/META-INF/resources/webjars/swagger-ui/4.11.1/swagger-initializer.js", resourcePattern);
    }

}