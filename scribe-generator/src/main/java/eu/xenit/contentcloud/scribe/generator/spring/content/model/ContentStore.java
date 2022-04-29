package eu.xenit.contentcloud.scribe.generator.spring.content.model;

import eu.xenit.contentcloud.scribe.generator.spring.data.model.TypeModel;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface ContentStore extends TypeModel {

    String entityClassName();

    ContentStore exportAsRestResource(Consumer<StoreRestResourceConfig> restResourceConfig);

    static ContentStore forEntity(String entityClassName) {
        return new ContentStoreImpl(entityClassName);
    }

    interface StoreRestResourceConfig {
        boolean isEnabled();
        StoreRestResourceConfig enable();
        StoreRestResourceConfig disable();
    }
}

@Accessors(fluent = true, chain = true)
class ContentStoreImpl implements ContentStore {

    @NonNull
    @Getter
    @Setter
    private String entityClassName;

    private StoreRestResourceConfig exportAsRestResource = new StoreRestResourceConfigImpl();

    public ContentStoreImpl(@NonNull String entityClassName) {
        this.entityClassName = entityClassName;
    }

    @Override
    public String className() {
        return this.entityClassName + "ContentStore";
    }

    @Override
    public ContentStore exportAsRestResource(Consumer<StoreRestResourceConfig> restResourceCustomizer) {
        restResourceCustomizer.accept(this.exportAsRestResource);
        return this;
    }

    static class StoreRestResourceConfigImpl implements StoreRestResourceConfig {

        @Getter
        @Accessors(fluent = false)
        private boolean enabled = true;

        @Override
        public StoreRestResourceConfig enable() {
            this.enabled = true;
            return this;
        }

        @Override
        public StoreRestResourceConfig disable() {
            this.enabled = false;
            return this;
        }
    }
}

