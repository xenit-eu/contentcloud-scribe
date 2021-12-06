package eu.xenit.contentcloud.scribe.changeset;

import java.net.URI;

public interface ChangeSetResolver {
    ChangeSet get(URI changeSet);
}
