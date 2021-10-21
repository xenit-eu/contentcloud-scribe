package io.spring.initializr.generator.language.java;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.language.TypeName;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Improvements over the original {@link JavaSourceCodeWriter} from Spring Initializr:
 *
 * <ul>
 *     <li>Add support for {@link JavaTypeDeclaration} {@code Modifier.INTERFACE}</li>
 *     <li>{@link #getUnqualifiedName(String)}} supports type parameters</li>
 * </ul>
 */
public class ScribeJavaSourceCodeWriter extends InitializrJavaSourceCodeWriter {

    public ScribeJavaSourceCodeWriter(IndentingWriterFactory indentingWriterFactory) {
        super(indentingWriterFactory);
    }

    protected void writeTo(SourceStructure structure, JavaCompilationUnit compilationUnit) throws IOException {
        Path output = structure.createSourceFile(compilationUnit.getPackageName(), compilationUnit.getName());
        Files.createDirectories(output.getParent());
        try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("java",
                Files.newBufferedWriter(output))) {
            writePackage(compilationUnit, writer);
            writeImports(compilationUnit, writer);

            for (JavaTypeDeclaration type : compilationUnit.getTypeDeclarations()) {
                writeTypeDeclaration(writer, type);
            }
        }
    }

    private void writeTypeDeclaration(IndentingWriter writer, JavaTypeDeclaration type) {

        writeAnnotations(writer, type);
        writeModifiers(writer, TYPE_MODIFIERS, type.getModifiers());

        if (Modifier.isInterface(type.getModifiers())) {
            writer.print("interface " + type.getName());
        } else {
            writer.print("class " + type.getName());
        }

        if (type.getExtends() != null) {
            writer.print(" extends " + getUnqualifiedName(type.getExtends()));
        }

        writer.println(" {");
        writer.println();
        List<JavaFieldDeclaration> fieldDeclarations = type.getFieldDeclarations();
        if (!fieldDeclarations.isEmpty()) {
            writer.indented(() -> {
                for (JavaFieldDeclaration fieldDeclaration : fieldDeclarations) {
                    writeFieldDeclaration(writer, fieldDeclaration);
                }
            });
        }
        List<JavaMethodDeclaration> methodDeclarations = type.getMethodDeclarations();
        if (!methodDeclarations.isEmpty()) {
            writer.indented(() -> {
                for (JavaMethodDeclaration methodDeclaration : methodDeclarations) {
                    writeMethodDeclaration(writer, methodDeclaration);
                }
            });
        }
        writer.println("}");
    }

    private void writeImports(JavaCompilationUnit compilationUnit, IndentingWriter writer) {
        Set<String> imports = determineImports(compilationUnit);
        if (!imports.isEmpty()) {
            for (String importedType : imports) {
                writer.println("import " + importedType + ";");
            }
            writer.println();
        }
    }

    private void writePackage(JavaCompilationUnit compilationUnit, IndentingWriter writer) {
        writer.println("package " + compilationUnit.getPackageName() + ";");
        writer.println();
    }

    @Override
    protected String getUnqualifiedName(String name) {
        return TypeName.of(name).getUnqualifiedName();
    }

    protected Set<String> determineImports(JavaCompilationUnit compilationUnit) {
        final List<String> imports = new ArrayList<>();
        for (JavaTypeDeclaration typeDeclaration : compilationUnit.getTypeDeclarations()) {
            if (requiresImport(typeDeclaration.getExtends())) {
                var extendsType = TypeName.of(typeDeclaration.getExtends());
                if (requiresImport(extendsType.getRawType().getTypeName())) {
                    imports.add(extendsType.getRawType().getTypeName());
                }

                // TODO make this recursive
                extendsType.getTypeParameters().stream()
                        .filter(typeParameter -> requiresImport(typeParameter.getTypeName()))
                        .forEach(typeName -> imports.add(typeName.getTypeName()));
            }

            imports.addAll(getRequiredImports(typeDeclaration.getAnnotations(), this::determineImports));

            for (JavaFieldDeclaration fieldDeclaration : typeDeclaration.getFieldDeclarations()) {
                if (requiresImport(fieldDeclaration.getReturnType())) {
                    imports.add(fieldDeclaration.getReturnType());
                }
                imports.addAll(getRequiredImports(fieldDeclaration.getAnnotations(), this::determineImports));
            }
            for (JavaMethodDeclaration methodDeclaration : typeDeclaration.getMethodDeclarations()) {
                if (requiresImport(methodDeclaration.getReturnType())) {
                    imports.add(methodDeclaration.getReturnType());
                }
                imports.addAll(getRequiredImports(methodDeclaration.getAnnotations(), this::determineImports));
                imports.addAll(getRequiredImports(methodDeclaration.getParameters(),
                        (parameter) -> Collections.singletonList(parameter.getType())));
                imports.addAll(getRequiredImports(
                        methodDeclaration.getStatements().stream().filter(JavaExpressionStatement.class::isInstance)
                                .map(JavaExpressionStatement.class::cast).map(JavaExpressionStatement::getExpression)
                                .filter(JavaMethodInvocation.class::isInstance).map(JavaMethodInvocation.class::cast),
                        (methodInvocation) -> Collections.singleton(methodInvocation.getTarget())));
            }
        }

        // remove all imports from current package again
        var result = imports.stream()
                .filter(importType -> {
                    String pkgName = TypeName.of(importType).getPackageName();
                    return !compilationUnit.getPackageName().equals(pkgName);
                })
                .sorted()
                .collect(Collectors.toList());


        Collections.sort(result);

        return new LinkedHashSet<>(result);
    }
}
