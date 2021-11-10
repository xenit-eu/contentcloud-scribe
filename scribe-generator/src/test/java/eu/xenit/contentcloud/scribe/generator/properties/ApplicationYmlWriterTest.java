package eu.xenit.contentcloud.scribe.generator.properties;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationYmlWriterTest {

    private IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory.withDefaultSettings();

    @Test
    void writeYmlContent() throws IOException {
        var props = new ApplicationProperties();
        props.put("server.forward-headers-strategy", "native");
        props.put("server.port", 8081);
        props.put("spring.flyway.locations", "classpath:db/migration/{vendor}");
        props.put("spring.data.rest.default-page-size", 100);
        props.put("spring.content.storage.type", "filesystem");

        StringWriter output = new StringWriter();
        try (IndentingWriter indentingWriter = this.indentingWriterFactory.createIndentingWriter("yml", output)) {
            new ApplicationYmlWriter().writeTo(indentingWriter, props);
        }
        String yml = output.toString();

        assertThat(yml).isEqualTo(Strings.join("" +
            "server:",
            "    forward-headers-strategy: native",
            "    port: 8081",
            "spring:",
            "    flyway:",
            "        locations: classpath:db/migration/{vendor}",
            "    data:",
            "        rest:",
            "            default-page-size: 100",
            "    content:",
            "        storage:",
            "            type: filesystem",
            ""
        ).with(System.lineSeparator()));
    }

    @Test
    void convertToTopics() {
        var props = new ApplicationProperties();
        props.put("spring.flyway.locations", "classpath:db/migration/{vendor}");
        props.put("spring.data.rest.default-page-size", 100);
        props.put("spring.content.storage.type", "filesystem");

        var root = ApplicationYmlWriter.byTopic(props);
        assertThat(root.topics())
                .hasSize(1)
                .singleElement()
                .satisfies(spring -> {
                    assertThat(spring.isLeaf()).isFalse();
                    assertThat(spring.getName()).isEqualTo("spring");
                    assertThat(spring.topics()).hasSize(3)
                            .satisfiesExactlyInAnyOrder(
                                    flyway -> assertThat(flyway.getName()).isEqualTo("flyway"),
                                    data -> {
                                        assertThat(data.getName()).isEqualTo("data");
                                        assertThat(data.get("rest")).isNotNull()
                                                .satisfies(rest -> assertThat(rest.get("default-page-size"))
                                                        .isNotNull()
                                                        .satisfies(pageSize -> {
                                                            assertThat(pageSize.isLeaf()).isTrue();
                                                            assertThat(pageSize.getValue()).isEqualTo("100");
                                                        }));
                                    },
                                    content -> assertThat(content.getName()).isEqualTo("content"));
                });
    }

}