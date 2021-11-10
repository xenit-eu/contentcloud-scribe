package eu.xenit.contentcloud.scribe.changeset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void testToCamelCase() {
        assertThat(Entity.camelToSnake("FooBar")).isEqualTo("foo_bar");
        assertThat(Entity.camelToSnake("FOO")).isEqualTo("foo");
        assertThat(Entity.camelToSnake("fooBAR")).isEqualTo("foo_bar");
        assertThat(Entity.camelToSnake("foo_bar")).isEqualTo("foo_bar");
    }
}