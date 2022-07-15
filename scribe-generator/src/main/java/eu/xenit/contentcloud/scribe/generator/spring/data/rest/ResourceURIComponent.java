package eu.xenit.contentcloud.scribe.generator.spring.data.rest;

import lombok.Value;

public interface ResourceURIComponent {
    boolean isVariable();
    String toUriTemplateComponent();

    static ResourceURIComponent variable(String variableName) {
        return new VariableResourceURIComponent(variableName);
    }

    static ResourceURIComponent path(String pathSegment) {
        return new StringResourceURIComponent(pathSegment);
    }
}

@Value
class StringResourceURIComponent implements ResourceURIComponent {
    String pathSegment;

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public String toUriTemplateComponent() {
        return pathSegment;
    }
}

@Value
class VariableResourceURIComponent implements ResourceURIComponent {
    String variableName;

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public String toUriTemplateComponent() {
        return '{'+variableName+'}';
    }
}

