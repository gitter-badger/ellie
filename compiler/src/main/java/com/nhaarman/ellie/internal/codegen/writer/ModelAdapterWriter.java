/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.ellie.internal.codegen.writer;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.nhaarman.ellie.annotation.Table;
import com.nhaarman.ellie.internal.ModelAdapter;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.ColumnElement;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class ModelAdapterWriter implements SourceWriter<TypeElement> {

    private static final EnumSet<Modifier> PUBLIC = EnumSet.of(Modifier.PUBLIC);
    private static final EnumSet<Modifier> PUBLIC_FINAL = EnumSet.of(Modifier.PUBLIC, Modifier.FINAL);

    private final Registry mRegistry;

    public ModelAdapterWriter(final Registry registry) {
        mRegistry = registry;
    }

    @Override
    public String createSourceName(final TypeElement element) {
        return "com.nhaarman.ellie." + createSimpleName(element);
    }

    @Override
    public void writeSource(final Writer writer, final TypeElement element) throws IOException {
        final String simpleName = createSimpleName(element);
        final String modelSimpleName = element.getSimpleName().toString();
        final String modelQualifiedName = element.getQualifiedName().toString();
        final String tableName = element.getAnnotation(Table.class).value();
        final Set<ColumnElement> columns = mRegistry.getColumnElements(element);

        JavaWriter javaWriter = new JavaWriter(writer);
        javaWriter.setCompressingTypes(true);
        javaWriter.setIndent("    ");

        javaWriter.emitSingleLineComment("Generated by Ellie. Do not modify!");
        javaWriter.emitPackage("com.nhaarman.ellie");

        writeImports(javaWriter, modelQualifiedName);

        javaWriter.beginType(simpleName, "class", PUBLIC_FINAL, null, "ModelAdapter<" + modelSimpleName + ">");
        javaWriter.emitEmptyLine();

        writeGetModelType(javaWriter, modelSimpleName);
        writeGetTableName(javaWriter, tableName);
        writeGetSchema(javaWriter, tableName, columns);

        javaWriter.endType();
    }

    private void writeImports(final JavaWriter writer, final String modelQualifiedName) throws IOException {
        writer.emitImports(
                modelQualifiedName,
                ModelAdapter.class.getName()
        );
        writer.emitEmptyLine();
    }

    private void writeGetModelType(final JavaWriter writer, final String modelSimpleName) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("Class<" + modelSimpleName + ">", "getModelType", PUBLIC);
        writer.emitStatement("return " + modelSimpleName + ".class");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeGetTableName(final JavaWriter writer, final String tableName) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod(String.class.getName(), "getTableName", PUBLIC);
        writer.emitStatement("return \"" + tableName + "\"");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeGetSchema(final JavaWriter writer, final String tableName, final Set<ColumnElement> columns) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod(String.class.getName(), "getSchema", PUBLIC);

        List<String> definitions = new ArrayList<String>();
        for (ColumnElement column : columns) {
            String schema = column.getSchema();
            if (!Strings.isNullOrEmpty(schema)) {
                definitions.add(schema);
            }
        }
        for (ColumnElement column : columns) {
            String foreignKeyClause = column.getForeignKeyClause();
            if (!Strings.isNullOrEmpty(foreignKeyClause)) {
                definitions.add(foreignKeyClause);
            }
        }

        writer.emitStatement(
                "return \"CREATE TABLE IF NOT EXISTS %s (%s)\"",
                tableName,
                Joiner.on(", ").join(definitions)
        );

        writer.endMethod();
        writer.emitEmptyLine();
    }

    private String createSimpleName(final TypeElement element) {
        return element.getSimpleName().toString() + "$$ModelAdapter";
    }
}
