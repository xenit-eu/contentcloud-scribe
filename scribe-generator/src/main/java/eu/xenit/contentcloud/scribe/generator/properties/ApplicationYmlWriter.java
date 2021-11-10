package eu.xenit.contentcloud.scribe.generator.properties;

import io.spring.initializr.generator.io.IndentingWriter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ApplicationYmlWriter implements ApplicationPropertiesWriter {

    @Override
    public String getFormat() {
        return "yml";
    }

    @Override
    public void writeTo(IndentingWriter writer, ApplicationProperties properties) {
        // get all the properties and convert to an hierarchical structure
        var root = byTopic(properties);
        root.topics().forEach(topic -> this.writeYmlNode(writer, topic));
    }

    private void writeYmlNode(IndentingWriter writer, PropertyNode node) {
        writer.print(node.getName());


        if (node.isLeaf()) {
            writer.print(": ");
            writer.println(node.getValue());
        } else {
            writer.println(":");
            writer.indented(() -> {
                node.topics().forEachOrdered(child -> this.writeYmlNode(writer, child));
            });
        }
    }

    static PropertyTopic byTopic(ApplicationProperties properties) {
        var root = new PropertyTopic("");
        properties.forEach(root::add);

        return root;
    }

    static abstract class PropertyNode {
        abstract String getName();
        abstract boolean isLeaf();
        abstract String getValue();

        abstract void add(@NonNull String key, String value);

        public abstract Stream<PropertyNode> topics();

        public abstract PropertyNode get(String key);
    }

    @RequiredArgsConstructor
    static final class PropertyTopic extends PropertyNode {

        private Map<String, PropertyNode> subs = new LinkedHashMap<>();

        @Getter
        @NonNull
        private final String name;

        public void add(@NonNull String key, String value) {
            if (key.isBlank()) {
                throw new IllegalArgumentException("key cannot be blank");
            }

            if (key.contains(".")) {
                String name = key.substring(0, key.indexOf("."));

                PropertyNode node = this.subs.computeIfAbsent(name, PropertyTopic::new);
                node.add(key.substring(key.indexOf(".") + 1), value);
            } else {
                String keySegment = key.substring(key.indexOf(".") + 1);
                this.subs.putIfAbsent(keySegment, new PropertyValue(keySegment, value));
            }
        }

        @Override
        public Stream<PropertyNode> topics() {
            return this.subs.values().stream();
        }

        @Override
        public PropertyNode get(String key) {
            return this.subs.get(key);
        }

        @Override
        boolean isLeaf() {
            return false;
        }

        @Override
        String getValue() {
            throw new UnsupportedOperationException();
        }
    }

    @RequiredArgsConstructor
    static class PropertyValue extends PropertyNode {

        @Getter
        private final String name;

        @Getter
        private final String value;

        @Override
        boolean isLeaf() {
            return true;
        }

        @Override
        void add(@NonNull String key, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<PropertyNode> topics() {
            return Stream.empty();
        }

        @Override
        public PropertyNode get(String key) {
            throw new UnsupportedOperationException();
        }
    }
}
