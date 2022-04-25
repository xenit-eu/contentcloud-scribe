package eu.xenit.contentcloud.scribe.changeset;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public Attribute(
            @JsonProperty("name") @NonNull String name,
            @JsonProperty("type") @NonNull String type,
            @JsonProperty("indexed") boolean indexed,
            @JsonProperty("naturalId") boolean naturalId,
            @JsonProperty("required") boolean required,
            @JsonProperty("unique") boolean unique
    ) {
        this.name = name;
        this.type = type;
        this.indexed = indexed;
        this.naturalId = naturalId;
        this.required = required;
        this.unique = unique;
    }

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

        private Builder type(String type) {
            return new Builder(this.name, "STRING");
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
