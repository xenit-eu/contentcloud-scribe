package eu.xenit.contentcloud.scribe.generator.data.source.java;

import eu.xenit.contentcloud.bard.JavaFile;
import eu.xenit.contentcloud.scribe.generator.data.source.SourceFile;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class JavaSourceFile implements SourceFile {

    @NonNull
    private final JavaFile javaFile;

    @Override
    public Path writeToPath(Path directory, Charset charset) throws IOException {
        return this.javaFile.writeToPath(directory, charset);
    }
}
