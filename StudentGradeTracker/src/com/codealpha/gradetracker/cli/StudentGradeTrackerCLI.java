package com.codealpha.gradetracker.cli;

import com.codealpha.gradetracker.model.ClassSummary;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.model.SubjectGrade;
import com.codealpha.gradetracker.service.GradeTrackerService;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class StudentGradeTrackerCLI {
    private final GradeTrackerService service;
    private final Scanner scanner;

    public StudentGradeTrackerCLI(GradeTrackerService service) {
        this.service = service != null ? service : new GradeTrackerService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option (0-9): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> viewAllStudents();
                case "2" -> addNewStudent();
                case "3" -> manageSubjectGrades();
                case "4" -> viewStudentReportCard();
                case "5" -> viewClassAnalytics();
                case "6" -> searchStudents();
                case "7" -> deleteStudent();
                case "8" -> exportToCSV();
                case "9" -> importFromCSV();
                case "0" -> {
                    System.out.println("\nThank you for using CodeAlpha Student Grade Tracker. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Error: Invalid option. Please enter a number between 0 and 9.");
            }
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void printBanner() {
        System.out.println("===============================================================================");
        System.out.println("            Bot CODEALPHA - STUDENT GRADE TRACKER (TASK 1)                     ");
        System.out.println("       Object-Oriented Academic Performance & Grade Analytics System           ");
        System.out.println("===============================================================================");
    }

    private void printMenu() {
        System.out.println("\n----------------------------- MAIN MENU -----------------------------");
        System.out.println(" [1] Bot View All Students & Overview Table");
        System.out.println(" [2] ? Add New Student");
        System.out.println(" [3] Bot Add / Update Subject Grades for a Student");
        System.out.println(" [4] Bot View Detailed Student Report Card");
        System.out.println(" [5] Bot View Class Performance Analytics & Summary Report");
        System.out.println(" [6] Bot Search Students (by ID, Name, or Grade)");
        System.out.println(" [7] ->Bot Delete Student Record");
        System.out.println(" [8] Bot Export Student Records to CSV");
        System.out.println(" [9] Bot Import Student Records from CSV");
        System.out.println(" [0] Bot Exit Application");
        System.out.println("---------------------------------------------------------------------");
    }

    private void viewAllStudents() {
        List<Student> list = service.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("Bot No student records found. Add students or load sample data.");
            return;
        }

        System.out.println("\n" + "=".repeat(88));
        System.out.printf("| %-10s | %-22s | %-8s | %-10s | %-6s | %-6s | %-8s |\n",
                "ID", "STUDENT NAME", "SUBJECTS", "AVG SCORE", "GPA", "GRADE", "STATUS");
        System.out.println("=".repeat(88));

        for (Student s : list) {
            System.out.printf("| %-10s | %-22s | %-8d | %9.1f%% | %-6.2f | %-6s | %-8s |\n",
                    s.getId(),
                    truncate(s.getName(), 22),
                    s.getGrades().size(),
                    s.getAverageScore(),
                    s.getGPA(),
                    s.getOverallLetterGrade(),
                    s.isPassed() ? "PASSED" : "FAILED");
        }
        System.out.println("=".repeat(88));
        System.out.printf("Total Enrolled Students: %d\n", list.size());
    }

    private void addNewStudent() {
        System.out.println("\n--- ? Add New Student ---");
        System.out.print("Enter Student ID (e.g. STU-107): ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Error: ? Student ID cannot be empty.");
            return;
        }

        if (service.findById(id).isPresent()) {
            System.out.println("Error: ? A student with ID '" + id + "' already exists.");
            return;
        }

        System.out.print("Enter Student Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Error: ? Student name cannot be empty.");
            return;
        }

        System.out.print("Enter Email Address (optional): ");
        String email = scanner.nextLine().trim();

        Student student = new Student(id, name, email);

        System.out.print("How many subject grades would you like to enter nowError: (0 for later): ");
        try {
            int count = Integer.parseInt(scanner.nextLine().trim());
            for (int i = 1; i <= count; i++) {
                System.out.printf("  Subject #%d Name: ", i);
                String sub = scanner.nextLine().trim();
                System.out.printf("  Subject #%d Score (0-100): ", i);
                double score = Double.parseDouble(scanner.nextLine().trim());
                student.addGrade(new SubjectGrade(sub, score));
            }
        } catch (Exception ex) {
            System.out.println("Bot Warning: Invalid numeric input. Grades can be added later.");
        }

        service.addStudent(student);
        System.out.println("Error: Student '" + name + "' (" + id + ") registered successfully!");
    }

    private void manageSubjectGrades() {
        System.out.println("\n--- Bot Manage Subject Grades ---");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        Student student = service.findById(id).orElse(null);

        if (student == null) {
            System.out.println("Error: Student not found with ID: " + id);
            return;
        }

        System.out.printf("Managing grades for: %s (%s)\n", student.getName(), student.getId());
        System.out.println("Current subjects:");
        for (SubjectGrade g : student.getGrades()) {
            System.out.printf("  ? %s\n", g);
        }

        System.out.print("\nEnter Subject Name: ");
        String subject = scanner.nextLine().trim();
        System.out.print("Enter Score (0.0 - 100.0): ");
        try {
            double score = Double.parseDouble(scanner.nextLine().trim());
            student.addGrade(new SubjectGrade(subject, score));
            service.updateStudent(student);
            System.out.println("Error: Grade saved successfully!");
            System.out.printf("Updated Avg: %.2f%% | GPA: %.2f | Grade: %s\n",
                    student.getAverageScore(), student.getGPA(), student.getOverallLetterGrade());
        } catch (Exception ex) {
            System.out.println("Error: ? Invalid score. " + ex.getMessage());
        }
    }

    private void viewStudentReportCard() {
        System.out.println("\n--- Bot Student Report Card ---");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        Student student = service.findById(id).orElse(null);

        if (student == null) {
            System.out.println("Error: Student not found with ID: " + id);
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("            ACADEMIC REPORT CARD                  ");
        System.out.println("=".repeat(50));
        System.out.printf("Student ID : %s\n", student.getId());
        System.out.printf("Full Name  : %s\n", student.getName());
        System.out.printf("Email      : %s\n", student.getEmail().isEmpty() ? "N/A" : student.getEmail());
        System.out.println("-".repeat(50));
        System.out.printf("%-24s | %-7s | %-6s | %-5s\n", "SUBJECT", "SCORE", "GRADE", "GP");
        System.out.println("-".repeat(50));

        for (SubjectGrade g : student.getGrades()) {
            System.out.printf("%-24s | %6.1f%% | %-6s | %-5.1f\n",
                    truncate(g.getSubjectName(), 24), g.getScore(), g.getLetterGrade(), g.getGradePoint());
        }

        System.out.println("-".repeat(50));
        System.out.printf("Cumulative Average Score : %6.2f%%\n", student.getAverageScore());
        System.out.printf("Grade Point Average (GPA): %6.2f / 4.00\n", student.getGPA());
        System.out.printf("Overall Letter Grade     : %s\n", student.getOverallLetterGrade());
        System.out.printf("Academic Standing        : %s\n", student.isPassed() ? "PASSED" : "FAILED");
        if (student.getHighestSubject() != null) {
            System.out.printf("Highest Subject Score    : %s (%.1f%%)\n",
                    student.getHighestSubject().getSubjectName(), student.getHighestSubject().getScore());
        }
        if (student.getLowestSubject() != null) {
            System.out.printf("Lowest Subject Score     : %s (%.1f%%)\n",
                    student.getLowestSubject().getSubjectName(), student.getLowestSubject().getScore());
        }
        System.out.println("=".repeat(50));
    }

    private void viewClassAnalytics() {
        ClassSummary summary = service.calculateClassSummary();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("       Bot CLASS PERFORMANCE SUMMARY & ANALYTICS            ");
        System.out.println("=".repeat(60));
        System.out.printf("Total Enrolled Students : %d\n", summary.getTotalStudents());
        System.out.printf("Class Average Score     : %.2f%%\n", summary.getClassAverageScore());
        System.out.printf("Class Average GPA       : %.2f / 4.00\n", summary.getClassAverageGPA());
        System.out.printf("Overall Pass Rate       : %.1f%%\n", summary.getPassRate());
        System.out.println("-".repeat(60));

        if (summary.getHighestScoringStudent() != null) {
            System.out.printf("Bot Highest Scorer        : %s (%s) - %.2f%%\n",
                    summary.getHighestScoringStudent().getName(),
                    summary.getHighestScoringStudent().getId(),
                    summary.getHighestScore());
        }
        if (summary.getLowestScoringStudent() != null) {
            System.out.printf("Bot Lowest Scorer         : %s (%s) - %.2f%%\n",
                    summary.getLowestScoringStudent().getName(),
                    summary.getLowestScoringStudent().getId(),
                    summary.getLowestScore());
        }

        System.out.println("-".repeat(60));
        System.out.println("Grade Distribution:");
        summary.getGradeDistribution().forEach((grade, count) -> {
            double pct = summary.getTotalStudents() > 0 ? ((double) count / summary.getTotalStudents() * 100.0) : 0.0;
            String bar = "->".repeat(Math.max(0, count * 3));
            System.out.printf("  Grade %-2s : %2d students (%5.1f%%)  %s\n", grade, count, pct, bar);
        });
        System.out.println("=".repeat(60));
    }

    private void searchStudents() {
        System.out.print("\nEnter search keyword (Name, ID, or Email): ");
        String q = scanner.nextLine().trim();
        List<Student> results = service.search(q, "ALL");

        System.out.printf("\nFound %d matching student(s):\n", results.size());
        for (Student s : results) {
            System.out.printf("  ? %-10s | %-20s | Avg: %5.1f%% | Grade: %s\n",
                    s.getId(), s.getName(), s.getAverageScore(), s.getOverallLetterGrade());
        }
    }

    private void deleteStudent() {
        System.out.print("\nEnter Student ID to delete: ");
        String id = scanner.nextLine().trim();
        if (service.deleteStudent(id)) {
            System.out.println("Error: Student record removed successfully.");
        } else {
            System.out.println("Error: No student found with ID: " + id);
        }
    }

    private void exportToCSV() {
        System.out.print("\nEnter filename to export (default: students_export.csv): ");
        String fname = scanner.nextLine().trim();
        if (fname.isEmpty()) fname = "students_export.csv";
        try {
            File f = new File(fname);
            service.exportToCSV(f);
            System.out.println("Error: Data exported successfully to: " + f.getAbsolutePath());
        } catch (Exception ex) {
            System.out.println("Error: Error exporting CSV: " + ex.getMessage());
        }
    }

    private void importFromCSV() {
        System.out.print("\nEnter CSV filename to import: ");
        String fname = scanner.nextLine().trim();
        try {
            File f = new File(fname);
            if (!f.exists()) {
                System.out.println("Error: File does not exist: " + f.getAbsolutePath());
                return;
            }
            int count = service.importFromCSV(f);
            System.out.printf("Error: Successfully imported %d student records.\n", count);
        } catch (Exception ex) {
            System.out.println("Error: Error importing CSV: " + ex.getMessage());
        }
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen - 2) + ".." : str;
    }
}
