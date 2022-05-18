package eu.xenit.contentcloud.scribe.infrastructure.changeset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectDto {
    private String name;
    private String organization;
    private String slug;
}
