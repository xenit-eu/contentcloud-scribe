package io.spring.initializr.generator.language.java;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ScribeJavaSourceCodeWriterTest {

    private static final Language LANGUAGE = new JavaLanguage();

    @TempDir
    Path directory;

    private final ScribeJavaSourceCodeWriter writer = new ScribeJavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());

    @Test
    void emptyTypeDeclarationWithSuperClass() throws IOException {
        JavaSourceCode sourceCode = new JavaSourceCode();
        JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
        JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
        test.extend("com.example.build.TestParent");
        List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
        assertThat(lines).containsExactly(
                "package com.example;",
                "",
                "import com.example.build.TestParent;",
                "",
                "class Test extends TestParent {",
                "",
                "}");
    }

    @Test
    void interfaceDeclaration_withSuperClassWithTypeParameters() throws IOException {

        JavaSourceCode sourceCode = new JavaSourceCode();
        JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "TestRepository");
        JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("TestRepository");

        test.modifiers(Modifier.INTERFACE);
        test.extend("org.springframework.data.jpa.repository.JpaRepository<com.example.Test, java.lang.Long>");

        List<String> lines = writeSingleType(sourceCode, "com/example/TestRepository.java");
        assertThat(lines).containsExactly(
                "package com.example;",
                "",
                "import org.springframework.data.jpa.repository.JpaRepository;",
                "",
                "interface TestRepository extends JpaRepository<Test, Long> {",
                "",
                "}");
    }


    private List<String> writeSingleType(JavaSourceCode sourceCode, String location) throws IOException {
        Path source = writeSourceCode(sourceCode).resolve(location);
        try (InputStream stream = Files.newInputStream(source)) {
            String[] lines = StreamUtils.copyToString(stream, StandardCharsets.UTF_8).split("\\r?\\n");
            return Arrays.asList(lines);
        }
    }

    private Path writeSourceCode(JavaSourceCode sourceCode) throws IOException {
        Path srcDirectory = this.directory.resolve(UUID.randomUUID().toString());
        SourceStructure sourceStructure = new SourceStructure(srcDirectory, LANGUAGE);
        this.writer.writeTo(sourceStructure, sourceCode);
        return sourceStructure.getSourcesDirectory();
    }

    @Test
    void getUnqualifiedName() {
        assertThat(writer.getUnqualifiedName("Test")).isEqualTo("Test");
        assertThat(writer.getUnqualifiedName("com.example.Test")).isEqualTo("Test");

        assertThat(writer.getUnqualifiedName("org.springframework.data.jpa.repository.JpaRepository<com.example.Test, java.lang.Long>"))
                .isEqualTo("JpaRepository<Test, Long>");


    }
}