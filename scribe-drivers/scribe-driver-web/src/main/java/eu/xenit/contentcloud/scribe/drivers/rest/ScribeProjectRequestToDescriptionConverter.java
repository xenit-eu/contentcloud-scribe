package eu.xenit.contentcloud.scribe.drivers.rest;

import eu.xenit.contentcloud.scribe.changeset.ChangeSetResolver;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import eu.xenit.contentcloud.scribe.changeset.ChangeSet;
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

    private final ChangeSetResolver changeSetResolver;

    DefaultProjectRequestToDescriptionConverter inner = new DefaultProjectRequestToDescriptionConverter();

    @Override
    public ProjectDescription convert(ScribeProjectRequest request, InitializrMetadata metadata) {
        var description = new ScribeProjectDescription();
        this.inner.convert(request, description, metadata);

        this.resolveChangeSet(request, this.changeSetResolver)
                .ifPresent(changeSet -> {
                    description.setChangeSet(changeSet);
                    description.setName(changeSet.getProject() + "-api");
                    // description.setApplicationName();
                    description.setGroupId(String.format("eu.xenit.contentcloud.userapps.%s",
                            asValidComponent(changeSet.getOrganization())));
                    description.setArtifactId(changeSet.getProject() + "-api");
                    description.setPackageName(description.getGroupId() + "." + asValidComponent(changeSet.getProject()));
                });

        if (request.isLombok()) {
            Dependency lombok = Optional.ofNullable(metadata.getDependencies().get("lombok")).orElseThrow();
            description.addDependency(lombok.getId(), MetadataBuildItemMapper.toDependency(lombok));
            description.useLombok(true);
        }


        return description;
    }

    static String asValidComponent(String organization) {
        return organization.replaceAll("-", "");
    }

    private Optional<ChangeSet> resolveChangeSet(ScribeProjectRequest request, ChangeSetResolver changeSetRepository) {
        try {
            return Optional.ofNullable(request.getChangeset())
                    .map(URI::create)
                    .map(changeSetRepository::get);
        } catch (IllegalArgumentException iae) {
            throw new InvalidProjectRequestException(iae.getMessage());
        }
    }

}
