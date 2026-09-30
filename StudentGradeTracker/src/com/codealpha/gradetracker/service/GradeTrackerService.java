package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.model.ClassSummary;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.model.SubjectGrade;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GradeTrackerService {
    private final List<Student> students = new ArrayList<>();

    public GradeTrackerService() {
        seedSampleData();
    }

    public synchronized List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public synchronized Optional<Student> findById(String id) {
        return students.stream()
                .filter(s -> s.getId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }

    public synchronized boolean addStudent(Student student) {
        if (student == null || student.getId().trim().isEmpty()) return false;
        if (findById(student.getId()).isPresent()) return false;
        students.add(student);
        return true;
    }

    public synchronized boolean updateStudent(Student updated) {
        if (updated == null) return false;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equalsIgnoreCase(updated.getId())) {
                students.set(i, updated);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteStudent(String id) {
        return students.removeIf(s -> s.getId().equalsIgnoreCase(id.trim()));
    }

    public synchronized void clearAll() {
        students.clear();
    }

    public synchronized List<Student> search(String query, String letterGradeFilter) {
        String q = query != null ? query.trim().toLowerCase() : "";
        String gradeFilter = letterGradeFilter != null ? letterGradeFilter.trim().toUpperCase() : "ALL";

        return students.stream()
                .filter(s -> {
                    boolean matchesQuery = q.isEmpty() ||
                            s.getName().toLowerCase().contains(q) ||
                            s.getId().toLowerCase().contains(q) ||
                            s.getEmail().toLowerCase().contains(q);

                    boolean matchesGrade = gradeFilter.equals("ALL") ||
                            s.getOverallLetterGrade().equalsIgnoreCase(gradeFilter);

                    return matchesQuery && matchesGrade;
                })
                .toList();
    }

    public synchronized ClassSummary calculateClassSummary() {
        if (students.isEmpty()) {
            Map<String, Integer> emptyDist = new LinkedHashMap<>();
            emptyDist.put("A", 0);
            emptyDist.put("B", 0);
            emptyDist.put("C", 0);
            emptyDist.put("D", 0);
            emptyDist.put("F", 0);
            return new ClassSummary(0, 0.0, 0.0, null, null, 0.0, 0.0, emptyDist, 0.0);
        }

        double totalScore = 0.0;
        double totalGPA = 0.0;
        Student highestStudent = null;
        Student lowestStudent = null;
        double highestScore = -1.0;
        double lowestScore = Double.MAX_VALUE;
        int passedCount = 0;

        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("A", 0);
        dist.put("B", 0);
        dist.put("C", 0);
        dist.put("D", 0);
        dist.put("F", 0);

        for (Student s : students) {
            double avg = s.getAverageScore();
            double gpa = s.getGPA();
            String lg = s.getOverallLetterGrade();

            totalScore += avg;
            totalGPA += gpa;

            if (dist.containsKey(lg)) {
                dist.put(lg, dist.get(lg) + 1);
            }

            if (s.isPassed()) {
                passedCount++;
            }

            if (avg > highestScore) {
                highestScore = avg;
                highestStudent = s;
            }
            if (avg < lowestScore) {
                lowestScore = avg;
                lowestStudent = s;
            }
        }

        double classAvgScore = totalScore / students.size();
        double classAvgGPA = totalGPA / students.size();
        double passRate = (double) passedCount / students.size() * 100.0;

        return new ClassSummary(students.size(), classAvgScore, classAvgGPA,
                highestStudent, lowestStudent,
                highestScore < 0 ? 0 : highestScore,
                lowestScore == Double.MAX_VALUE ? 0 : lowestScore,
                dist, passRate);
    }

    public synchronized void seedSampleData() {
        students.clear();

        Student s1 = new Student("STU-101", "Emma Watson", "emma.watson@university.edu");
        s1.addGrade(new SubjectGrade("Mathematics", 95.0));
        s1.addGrade(new SubjectGrade("Data Structures", 92.5));
        s1.addGrade(new SubjectGrade("Database Systems", 89.0));
        s1.addGrade(new SubjectGrade("Software Engineering", 94.0));
        students.add(s1);

        Student s2 = new Student("STU-102", "Alex Johnson", "alex.j@university.edu");
        s2.addGrade(new SubjectGrade("Mathematics", 82.0));
        s2.addGrade(new SubjectGrade("Data Structures", 78.5));
        s2.addGrade(new SubjectGrade("Database Systems", 85.0));
        s2.addGrade(new SubjectGrade("Software Engineering", 80.0));
        students.add(s2);

        Student s3 = new Student("STU-103", "Sophia Martinez", "sophia.m@university.edu");
        s3.addGrade(new SubjectGrade("Mathematics", 74.0));
        s3.addGrade(new SubjectGrade("Data Structures", 71.0));
        s3.addGrade(new SubjectGrade("Database Systems", 76.5));
        s3.addGrade(new SubjectGrade("Software Engineering", 70.0));
        students.add(s3);

        Student s4 = new Student("STU-104", "Daniel Craig", "daniel.c@university.edu");
        s4.addGrade(new SubjectGrade("Mathematics", 64.0));
        s4.addGrade(new SubjectGrade("Data Structures", 62.0));
        s4.addGrade(new SubjectGrade("Database Systems", 68.0));
        s4.addGrade(new SubjectGrade("Software Engineering", 65.5));
        students.add(s4);

        Student s5 = new Student("STU-105", "Liam Neeson", "liam.n@university.edu");
        s5.addGrade(new SubjectGrade("Mathematics", 52.0));
        s5.addGrade(new SubjectGrade("Data Structures", 48.0));
        s5.addGrade(new SubjectGrade("Database Systems", 55.0));
        s5.addGrade(new SubjectGrade("Software Engineering", 50.0));
        students.add(s5);

        Student s6 = new Student("STU-106", "Olivia Rodrigo", "olivia.r@university.edu");
        s6.addGrade(new SubjectGrade("Mathematics", 98.0));
        s6.addGrade(new SubjectGrade("Data Structures", 96.0));
        s6.addGrade(new SubjectGrade("Database Systems", 94.0));
        s6.addGrade(new SubjectGrade("Software Engineering", 97.5));
        students.add(s6);
    }

    public synchronized void exportToCSV(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("StudentID,Name,Email,Subject,Score");
            writer.newLine();
            for (Student s : students) {
                if (s.getGrades().isEmpty()) {
                    String line = "\"" + escapeCsv(s.getId()) + "\",\"" + escapeCsv(s.getName()) + "\",\"" + escapeCsv(s.getEmail()) + "\",\"\",0.0";
                    writer.write(line);
                    writer.newLine();
                } else {
                    for (SubjectGrade g : s.getGrades()) {
                        String line = "\"" + escapeCsv(s.getId()) + "\",\"" + escapeCsv(s.getName()) + "\",\"" + escapeCsv(s.getEmail()) + "\",\"" + escapeCsv(g.getSubjectName()) + "\"," + String.format(Locale.US, "%.2f", g.getScore());
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }
    }

    public synchronized int importFromCSV(File file) throws IOException {
        Map<String, Student> map = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Header
            if (line == null) return 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = parseCsvLine(line);
                if (tokens.length >= 3) {
                    String id = tokens[0].trim();
                    String name = tokens[1].trim();
                    String email = tokens[2].trim();
                    Student s = map.computeIfAbsent(id, k -> new Student(id, name, email));

                    if (tokens.length >= 5) {
                        String subject = tokens[3].trim();
                        String scoreStr = tokens[4].trim();
                        if (!subject.isEmpty() && !scoreStr.isEmpty()) {
                            try {
                                double score = Double.parseDouble(scoreStr);
                                s.addGrade(new SubjectGrade(subject, score));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        }

        if (!map.isEmpty()) {
            students.clear();
            students.addAll(map.values());
        }
        return map.size();
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
    }

    private String[] parseCsvLine(String line) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                list.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        list.add(sb.toString().trim());
        return list.toArray(new String[0]);
    }
}
