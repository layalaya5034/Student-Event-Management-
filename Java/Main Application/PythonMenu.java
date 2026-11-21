package com.smartcampus.python;

import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PythonMenu {

    private static String pythonCommand = null;

    static {
        detectPythonExecutable();
    }

    private static void detectPythonExecutable() {
        String[] candidates = {
            "C:\\Users\\sugirthan\\AppData\\Local\\Programs\\Python\\Python313\\python.exe",
            "python",
            "py",
            "python3"
        };
        for (String c : candidates) {
            try {
                ProcessBuilder pb = new ProcessBuilder(c, "--version");
                Process p = pb.start();
                if (p.waitFor() == 0) {
                    pythonCommand = c;
                    return;
                }
            } catch (Exception ignored) {}
        }
        // Fallback
        pythonCommand = "python";
    }

    public static void showMenu() {
        DualLogger.setModule("PythonAnalysis");
        while (true) {
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("                    PYTHON DATA ANALYSIS");
            DualLogger.println("============================================================");
            DualLogger.println("1.  Event Registration Distribution");
            DualLogger.println("2.  Daily Registration Trends");
            DualLogger.println("3.  Monthly Event Trends");
            DualLogger.println("4.  Student Participation Analysis");
            DualLogger.println("5.  Department-wise Participation Analysis");
            DualLogger.println("6.  Year-wise Student Participation");
            DualLogger.println("7.  Event Popularity Analysis");
            DualLogger.println("8.  Most Registered Events");
            DualLogger.println("9.  Least Registered Events");
            DualLogger.println("10. Attendance Distribution");
            DualLogger.println("11. Event-wise Attendance Percentage");
            DualLogger.println("12. Top Participating Students");
            DualLogger.println("13. Event Capacity Utilization");
            DualLogger.println("14. Event Type Distribution");
            DualLogger.println("15. Registration vs Attendance Analysis");
            DualLogger.println("16. Certificate Eligibility Analysis");
            DualLogger.println("17. Complete Data Analysis");
            DualLogger.println("18. Return to Main Menu");
            DualLogger.println("============================================================");

            int choice = InputReader.readInt("Enter your choice: ", 1, 18);
            if (choice == 18) {
                DualLogger.println("[INFO] Returning to Main Menu...");
                DualLogger.setModule("SmartCampusEventManagement");
                break;
            }

            executePythonOption(choice);
            InputReader.pause();
        }
    }

    private static void executePythonOption(int choice) {
        String scriptPath = findScriptPath();
        if (scriptPath == null) {
            DualLogger.error("Python analysis runner script 'run_python_analysis.py' could not be found!");
            return;
        }

        DualLogger.println("[INFO] Launching Python engine via: " + pythonCommand);
        List<String> command = new ArrayList<>();
        command.add(pythonCommand);
        command.add(scriptPath);
        command.add("--option=" + choice);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    DualLogger.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                DualLogger.success("Python analysis executed successfully.");
            } else {
                DualLogger.error("Python analysis exited with error code: " + exitCode);
            }
        } catch (Exception e) {
            DualLogger.error("Failed to execute Python process: " + e.getMessage());
        }
    }

    private static String findScriptPath() {
        String[] candidates = {
            "Python/run_python_analysis.py",
            "../Python/run_python_analysis.py",
            "../../Python/run_python_analysis.py",
            "C:\\Student event management\\Python\\run_python_analysis.py"
        };
        for (String c : candidates) {
            File f = new File(c);
            if (f.exists()) {
                return f.getAbsolutePath();
            }
        }
        return null;
    }
}
