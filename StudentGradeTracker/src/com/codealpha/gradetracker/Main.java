package com.codealpha.gradetracker;

import com.codealpha.gradetracker.cli.StudentGradeTrackerCLI;
import com.codealpha.gradetracker.service.GradeTrackerService;
import com.codealpha.gradetracker.ui.StudentGradeTrackerGUI;

import javax.swing.*;
import java.awt.GraphicsEnvironment;

public class Main {
    public static void main(String[] args) {
        GradeTrackerService service = new GradeTrackerService();

        boolean forceCli = false;
        boolean forceGui = false;

        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg)) forceCli = true;
            if ("--gui".equalsIgnoreCase(arg)) forceGui = true;
        }

        boolean isHeadless = GraphicsEnvironment.isHeadless();

        if (forceCli || isHeadless) {
            System.out.println("Starting in Console CLI mode...");
            new StudentGradeTrackerCLI(service).start();
        } else {
            System.out.println("Launching Student Grade Tracker Desktop GUI...");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                StudentGradeTrackerGUI gui = new StudentGradeTrackerGUI(service);
                gui.setVisible(true);
            });
        }
    }
}
