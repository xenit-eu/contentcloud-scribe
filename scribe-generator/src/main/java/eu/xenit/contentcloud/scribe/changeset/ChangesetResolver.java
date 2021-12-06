package eu.xenit.contentcloud.scribe.changeset;

import java.net.URI;

public interface ChangesetResolver {
    Changeset get(URI changeset);
}
