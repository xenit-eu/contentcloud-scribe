package eu.xenit.contentcloud.scribe.generator.opa;

import eu.xenit.contentcloud.scribe.changeset.Policy;
import eu.xenit.contentcloud.scribe.generator.ScribeProjectDescription;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpaPolicyProjectContributor implements ProjectContributor {
    private final ScribeProjectDescription description;
    private final IndentingWriterFactory indentingWriterFactory;

    @Override
    public void contribute(Path projectRoot) throws IOException {
        SourceStructure mainSource = description.getBuildSystem().getMainSource(projectRoot, description.getLanguage());
        Path mainResourcesDir = projectRoot.resolve(mainSource.getResourcesDirectory());
        Path staticResourcesDir = Files.createDirectories(mainResourcesDir.resolve("META-INF/resources"));
        Path regoPolicy = staticResourcesDir.resolve("policy.rego");

        try(var output = indentingWriterFactory.createIndentingWriter("rego", Files.newBufferedWriter(regoPolicy))) {
            var regoWriter = new OpaRegoWriter(output);

            for(Policy policy: description.getChangeset().getAfterModel().getPolicies()) {
                regoWriter.writePolicy(policy);
            }
        }



    }
}
