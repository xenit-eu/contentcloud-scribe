package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import lombok.Data;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ChangesetRepositoryProperties {

    private final List<String> allowList = new ArrayList<>(List.of("http://localhost:8080/orgs/*/projects/*/changesets/*"));

    public Set<PathPattern> getAllowedPaths() {
        return this.getAllowList().stream()
                .map(pattern -> new PathPatternParser().parse(pattern))
                .collect(Collectors.toSet());
    }
}
