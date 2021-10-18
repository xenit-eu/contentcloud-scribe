package eu.xenit.contentcloud.scribe.drivers.rest;

import eu.xenit.contentcloud.scribe.changeset.ChangeSetResolver;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.changeset.ChangeSet;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.project.DefaultProjectRequestToDescriptionConverter;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ScribeProjectRequestToDescriptionConverter
        implements ProjectRequestToDescriptionConverter<ScribeProjectRequest> {

    private final ChangeSetResolver changeSetResolver;

    DefaultProjectRequestToDescriptionConverter inner = new DefaultProjectRequestToDescriptionConverter();

    @Override
    public ProjectDescription convert(ScribeProjectRequest request, InitializrMetadata metadata) {
        var description = new ScribeProjectDescription();
        this.inner.convert(request, description, metadata);

        this.resolveChangeSet(request, this.changeSetResolver)
                .ifPresent(description::setChangeSet);

        return description;
    }

    private Optional<ChangeSet> resolveChangeSet(ScribeProjectRequest request, ChangeSetResolver changeSetRepository) {
        return Optional.ofNullable(request.getChangeset())
                .map(changeSetRepository::get);
    }


}
