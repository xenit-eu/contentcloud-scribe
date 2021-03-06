package eu.xenit.contentcloud.scribe.changeset;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
public class Attribute {

    @NonNull String name;

    @NonNull String type;

    boolean indexed;
    boolean naturalId;
    boolean required;
    boolean unique;

    public static AttributeTypeBuilder builder(String name) {
        return new AttributeTypeBuilder(name);
    }

    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AttributeTypeBuilder {

        private final String name;

        public Builder string() {
            return this.type("STRING");
        }

        public Builder content() {
            return this.type("CONTENT");
        }

        public Builder bool() { return this.type("BOOLEAN"); }

        public Builder uuid() {
            return this.type("UUID");
        }

        public Builder timestamp() {
            return this.type("TIMESTAMP");
        }

        public Builder number() {
            return this.type("NUMBER");
        }

        public Builder type(String type) {
            return new Builder(this.name, type);
        }
    }

    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private final String name;
        private final String type;

        @Setter
        private boolean indexed;
        @Setter
        private boolean naturalId;
        @Setter
        private boolean required;
        @Setter
        private boolean unique;

        public Attribute build() {
            return new Attribute(this.name, this.type, this.indexed, this.naturalId, this.required, this.unique);
        }

    }

}