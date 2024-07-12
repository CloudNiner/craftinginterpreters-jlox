package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output_directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println("package com.craftinginterpreters;");
        writer.println("");
        writer.println("abstract class " + baseName + " {");

        // TODO: Implement
        for (String type: types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        int indentLevel = 1;
        writer.println(indent(indentLevel++) + "class " + className + " extends " + baseName + " {");

        // Constructor
        writer.println(indent(indentLevel++) + className + "(" + fieldList + ") {");
        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println(indent(indentLevel) + "this." + name + " = " + name + ";");
        }
        writer.println(indent(--indentLevel) + "}");

        // Fields
        writer.println();
        for (String field : fields) {
            writer.println(indent(indentLevel) + "final " + field + ";");
        }

        writer.println(indent(--indentLevel) + "}");
    }

    private static String indent(int level) {
        return " ".repeat(level * 4);
    }
}
