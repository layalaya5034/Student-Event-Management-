package com.smartcampus.util;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * InputReader handles interactive user input from Windows CMD,
 * validates types and boundaries, and ensures robust error handling.
 */
public class InputReader {
    private static final Scanner scanner = new Scanner(System.in);

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            DualLogger.print(prompt);
            if (!scanner.hasNextLine()) {
                DualLogger.println();
                return max; // Return exit/return option on EOF
            }
            String line = scanner.nextLine().trim();
            DualLogger.logInput(prompt, line);
            if (line.isEmpty()) {
                DualLogger.warning("Input cannot be empty. Please enter a valid number.");
                continue;
            }
            try {
                int val = Integer.parseInt(line);
                if (val < min || val > max) {
                    DualLogger.warning(String.format("Value must be between %d and %d. Please try again.", min, max));
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                DualLogger.error("Invalid numeric input. Please enter a whole number.");
            }
        }
    }

    public static String readString(String prompt, boolean allowEmpty) {
        while (true) {
            DualLogger.print(prompt);
            if (!scanner.hasNextLine()) {
                DualLogger.println();
                return "";
            }
            String line = scanner.nextLine().trim();
            DualLogger.logInput(prompt, line);
            if (!allowEmpty && line.isEmpty()) {
                DualLogger.warning("This field cannot be empty. Please enter a value.");
                continue;
            }
            return line;
        }
    }

    public static String readDate(String prompt, String defaultDate) {
        while (true) {
            DualLogger.print(prompt + (defaultDate != null ? " [" + defaultDate + "]: " : ": "));
            if (!scanner.hasNextLine()) {
                DualLogger.println();
                return defaultDate != null ? defaultDate : LocalDate.now().toString();
            }
            String line = scanner.nextLine().trim();
            DualLogger.logInput(prompt, line);
            if (line.isEmpty() && defaultDate != null) {
                return defaultDate;
            }
            try {
                LocalDate parsed = LocalDate.parse(line);
                return parsed.toString();
            } catch (DateTimeParseException e) {
                DualLogger.error("Invalid date format. Expected YYYY-MM-DD (e.g. 2026-11-25).");
            }
        }
    }

    public static void pause() {
        DualLogger.print("\nPress Enter to continue...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
        DualLogger.println();
    }
}
