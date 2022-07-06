package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityImpl.JpaEntityNaming;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JpaEntityImplTest {

    @Nested
    class JpaEntityNamingTests {

        @Test
        void testClassNames() {
            assertThat(JpaEntityNaming.from("fooBar").className()).isEqualTo("FooBar");
            assertThat(JpaEntityNaming.from("foo-bar").className()).isEqualTo("FooBar");
            assertThat(JpaEntityNaming.from("foo123bar").className()).isEqualTo("Foo123Bar");
            assertThat(JpaEntityNaming.from("foo-v2-bar").className()).isEqualTo("FooV2Bar");

            // consecutive hyphens
            assertThatThrownBy(() -> JpaEntityNaming.from("foo---bar")).isInstanceOf(IllegalArgumentException.class);

            // too short
            assertThatThrownBy(() -> JpaEntityNaming.from("f")).isInstanceOf(IllegalArgumentException.class);

            // must start with a letter
            assertThatThrownBy(() -> JpaEntityNaming.from("123foobar")).isInstanceOf(IllegalArgumentException.class);

            // cannot start or end with a hypen
            assertThatThrownBy(() -> JpaEntityNaming.from("-foobar")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> JpaEntityNaming.from("foobar-")).isInstanceOf(IllegalArgumentException.class);
        }


    }
}
