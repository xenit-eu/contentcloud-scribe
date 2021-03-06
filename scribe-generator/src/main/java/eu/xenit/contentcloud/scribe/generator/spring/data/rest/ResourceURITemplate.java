package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;

@Value
public class ResourceURITemplate {
    List<ResourceURIComponent> components;

    public String toUriTemplate() {
        return components.stream().map(ResourceURIComponent::toUriTemplateComponent).collect(Collectors.joining("/"));
    }

    public static ResourceURITemplate of(ResourceURIComponent... components) {
        return new ResourceURITemplate(Arrays.asList(components));
    }

    public ResourceURITemplate slash(ResourceURIComponent component) {
        var components = new ArrayList<>(this.components);
        components.add(component);
        return new ResourceURITemplate(components);
    }
}
