package eu.xenit.contentcloud.scribe.drivers.rest;

import eu.xenit.contentcloud.scribe.changeset.ChangesetResolver;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.changeset.Changeset;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import io.spring.initializr.web.project.DefaultProjectRequestToDescriptionConverter;
import io.spring.initializr.web.project.InvalidProjectRequestException;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
public class ScribeProjectRequestToDescriptionConverter
        implements ProjectRequestToDescriptionConverter<ScribeProjectRequest> {

    private final ChangesetResolver changesetResolver;

    DefaultProjectRequestToDescriptionConverter inner = new DefaultProjectRequestToDescriptionConverter();

    @Override
    public ProjectDescription convert(ScribeProjectRequest request, InitializrMetadata metadata) {
        var description = new ScribeProjectDescription();
        this.inner.convert(request, description, metadata);

        this.resolveChangeset(request, this.changesetResolver)
                .ifPresent(changeset -> {
                    description.setChangeset(changeset);
                    description.setName(changeset.getProject() + "-api");
                    // description.setApplicationName();
                    description.setGroupId(String.format("eu.xenit.contentcloud.userapps.%s",
                            asValidComponent(changeset.getOrganization())));
                    description.setArtifactId(changeset.getProject() + "-api");
                    description.setPackageName(description.getGroupId() + "." + asValidComponent(changeset.getProject()));
                });

        description.useLombok(request.isLombok());

        return description;
    }

    static String asValidComponent(String organization) {
        return organization.replaceAll("-", "");
    }

    private Optional<Changeset> resolveChangeset(ScribeProjectRequest request, ChangesetResolver changeSetRepository) {
        try {
            return Optional.ofNullable(request.getChangeset())
                    .map(URI::create)
                    .map(changeSetRepository::get);
        } catch (IllegalArgumentException iae) {
            throw new InvalidProjectRequestException(iae.getMessage());
        }
    }

}
