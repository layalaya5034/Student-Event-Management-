package com.smartcampus.util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DualLogger provides real-time dual output:
 * 1. Outputs directly to the Windows CMD console.
 * 2. Simultaneously appends to the corresponding module log file in the Output folder.
 * Standard input (Scanner) remains completely independent and interactive.
 */
public class DualLogger {
    private static String currentModule = "SmartCampusEventManagement";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static File outputDir = null;

    static {
        // Find or create Output directory
        String[] possiblePaths = {
            "Output",
            "../Output",
            "../../Output",
            "C:\\Student event management\\Output"
        };
        for (String p : possiblePaths) {
            File f = new File(p);
            if (f.exists() && f.isDirectory()) {
                outputDir = f;
                break;
            }
        }
        if (outputDir == null) {
            outputDir = new File("Output");
            outputDir.mkdirs();
        }
    }

    public static synchronized void setModule(String moduleName) {
        currentModule = moduleName;
    }

    public static synchronized String getModule() {
        return currentModule;
    }

    public static synchronized void print(String text) {
        System.out.print(text);
        writeToFile(text);
    }

    public static synchronized void println(String text) {
        System.out.println(text);
        writeToFile(text + System.lineSeparator());
    }

    public static synchronized void println() {
        System.out.println();
        writeToFile(System.lineSeparator());
    }

    public static synchronized void printf(String format, Object... args) {
        String formatted = String.format(format, args);
        System.out.print(formatted);
        writeToFile(formatted);
    }

    public static synchronized void success(String message) {
        println("[SUCCESS] " + message);
    }

    public static synchronized void warning(String message) {
        println("[WARNING] " + message);
    }

    public static synchronized void error(String message) {
        println("[ERROR] " + message);
    }

    public static synchronized void logInput(String prompt, String userInput) {
        writeToFile("[INPUT] " + prompt + " -> " + userInput + System.lineSeparator());
    }

    public static synchronized void header(String title) {
        println("============================================================");
        println(centerText(title, 60));
        println("============================================================");
    }

    public static synchronized void subHeader(String title) {
        println("-------------------- " + title + " --------------------");
    }

    public static String centerText(String text, int width) {
        if (text == null) text = "";
        int padding = width - text.length();
        if (padding <= 0) return text;
        int left = padding / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) sb.append(" ");
        sb.append(text);
        return sb.toString();
    }

    private static void writeToFile(String text) {
        try {
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File outFile = new File(outputDir, currentModule + "_Output.txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(outFile, true))) {
                pw.print(text);
                pw.flush();
            }
        } catch (IOException e) {
            // Silently fallback without interrupting console execution
        }
    }
}
