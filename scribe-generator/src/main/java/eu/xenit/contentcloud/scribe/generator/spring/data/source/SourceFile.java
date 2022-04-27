package eu.xenit.contentcloud.scribe.generator.spring.data.source;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface SourceFile {

    /**
     * Writes this to {@code directory} with the provided {@code charset} using the standard directory
     * structure.
     * Returns the {@link Path} instance to which source is actually written.
     */
    Path writeToPath(Path directory, Charset charset) throws IOException;

    /**
     * Writes this to {@code directory} as UTF-8 using the standard directory structure.
     */
    default void writeTo(Path directory) throws IOException {
        writeToPath(directory);
    }

    /**
     * Writes this to {@code directory} with the provided {@code charset} using the standard directory
     * structure.
     */
    default void writeTo(Path directory, Charset charset) throws IOException {
        writeToPath(directory, charset);
    }

    /**
     * Writes this to {@code directory} as UTF-8 using the standard directory structure.
     * Returns the {@link Path} instance to which source is actually written.
     */
    default Path writeToPath(Path directory) throws IOException {
        return writeToPath(directory, UTF_8);
    }
}
