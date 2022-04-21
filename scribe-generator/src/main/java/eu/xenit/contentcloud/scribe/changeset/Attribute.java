package eu.xenit.contentcloud.scribe.changeset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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

    public static Builder withName(String name) {
        return new Builder(name);
    }

    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private final String name;

        @Setter
        private boolean indexed;
        @Setter
        private boolean naturalId;
        @Setter
        private boolean required;
        @Setter
        private boolean unique;

        public Attribute type(@NonNull String type) {
            return new Attribute(name, type, this.indexed, this.naturalId, this.required, this.unique);
        }

        public Attribute typeString() {
            return this.type("STRING");
        }

    }

}
