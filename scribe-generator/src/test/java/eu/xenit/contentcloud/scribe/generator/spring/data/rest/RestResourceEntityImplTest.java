package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.contentcloud.scribe.changeset.Entity;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RestResourceEntityImplTest {
    @Nested
    class EntityNamingTests {

        @Test
        void testResourceNamingSimple() {
            var entity = Entity.builder()
                    .name("foot")
                    .build();
            var restEntity = RestResourceEntity.forEntity(entity);

            assertThat(restEntity.getPathSegment()).isEqualTo("feet");
            assertThat(restEntity.getItemResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("foot");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("feet"),
                                ResourceURIComponent.variable("id")
                        )
                );
            });
            assertThat(restEntity.getCollectionResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("feet");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("feet")
                        )
                );
            });
        }

        @Test
        void testResourceNamingMultipart() {
            var entity = Entity.builder()
                    .name("foot-bridge")
                    .build();
            var restEntity = RestResourceEntity.forEntity(entity);

            assertThat(restEntity.getPathSegment()).isEqualTo("foot-bridges");
            assertThat(restEntity.getItemResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("foot-bridge");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("foot-bridges"),
                                ResourceURIComponent.variable("id")
                        )
                );
            });
            assertThat(restEntity.getCollectionResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("foot-bridges");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("foot-bridges")
                        )
                );
            });
        }

        @Test
        void testResourceNamingCapitalized() {
            var entity = Entity.builder()
                    .name("FootBridge")
                    .build();
            var restEntity = RestResourceEntity.forEntity(entity);

            assertThat(restEntity.getPathSegment()).isEqualTo("FootBridges");
            assertThat(restEntity.getItemResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("FootBridge");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("FootBridges"),
                                ResourceURIComponent.variable("id")
                        )
                );
            });
            assertThat(restEntity.getCollectionResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("FootBridges");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("FootBridges")
                        )
                );
            });
        }
    }

    @Nested
    class JpaEntityNamingTests {

        @Test
        void testResourceNamingSimple() {
            var entity = Entity.builder()
                    .name("foot")
                    .build();
            var jpaEntity = JpaEntity.withName(entity.getName());
            var restEntity = RestResourceEntity.forSpringDefaults(jpaEntity);

            assertThat(restEntity.getPathSegment()).isEqualTo("feet");
            assertThat(restEntity.getItemResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("foot");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("feet"),
                                ResourceURIComponent.variable("id")
                        )
                );
            });
            assertThat(restEntity.getCollectionResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("feet");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("feet")
                        )
                );
            });
        }

        @Test
        void testResourceNamingMultipart() {
            var entity = Entity.builder()
                    .name("foot-bridge")
                    .build();
            var jpaEntity = JpaEntity.withName(entity.getName());
            var restEntity = RestResourceEntity.forSpringDefaults(jpaEntity);

            assertThat(restEntity.getPathSegment()).isEqualTo("footBridges");
            assertThat(restEntity.getItemResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("footBridge");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("footBridges"),
                                ResourceURIComponent.variable("id")
                        )
                );
            });
            assertThat(restEntity.getCollectionResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("footBridges");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("footBridges")
                        )
                );
            });
        }

        @Test
        void testResourceNamingCapitalized() {
            var entity = Entity.builder()
                    .name("FootBridge")
                    .build();
            var jpaEntity = JpaEntity.withName(entity.getName());
            var restEntity = RestResourceEntity.forSpringDefaults(jpaEntity);

            assertThat(restEntity.getPathSegment()).isEqualTo("footBridges");
            assertThat(restEntity.getItemResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("footBridge");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("footBridges"),
                                ResourceURIComponent.variable("id")
                        )
                );
            });
            assertThat(restEntity.getCollectionResource()).satisfies(itemResource -> {
                assertThat(itemResource.getRelationName()).isEqualTo("footBridges");
                assertThat(itemResource.getUriTemplate()).isEqualTo(
                        ResourceURITemplate.of(
                                ResourceURIComponent.path("footBridges")
                        )
                );
            });
        }
    }
}