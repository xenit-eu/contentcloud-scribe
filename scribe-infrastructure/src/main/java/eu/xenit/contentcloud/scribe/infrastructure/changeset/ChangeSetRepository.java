package eu.xenit.contentcloud.scribe.infrastructure.changeset;

import eu.xenit.contentcloud.scribe.changeset.ChangeSetResolver;
import eu.xenit.contentcloud.scribe.changeset.ChangeSet;
import org.springframework.web.client.RestTemplate;

public class ChangeSetRepository implements ChangeSetResolver {

    private final RestTemplate restTemplate;

    public ChangeSetRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ChangeSet get(String changeSet) {
        var result = this.restTemplate.getForObject("http://localhost:8080/changesets/{changeSetId}", ChangeSet.class, changeSet);
        return result;
    }
}
