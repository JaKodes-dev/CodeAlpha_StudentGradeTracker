package com.codealpha.gradetracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private final List<SubjectGrade> grades;

    public Student(String id, String name, String email) {
        this.id = id != null ? id.trim() : "";
        this.name = name != null ? name.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.grades = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id != null ? id.trim() : ""; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name != null ? name.trim() : ""; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email.trim() : ""; }

    public List<SubjectGrade> getGrades() { return Collections.unmodifiableList(grades); }

    public void addGrade(SubjectGrade grade) {
        if (grade != null) {
            for (int i = 0; i < grades.size(); i++) {
                if (grades.get(i).getSubjectName().equalsIgnoreCase(grade.getSubjectName())) {
                    grades.set(i, grade);
                    return;
                }
            }
            grades.add(grade);
        }
    }

    public boolean removeGrade(String subjectName) {
        return grades.removeIf(g -> g.getSubjectName().equalsIgnoreCase(subjectName));
    }

    public void clearGrades() { grades.clear(); }

    public double getAverageScore() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0.0;
        for (SubjectGrade g : grades) sum += g.getScore();
        return Math.round((sum / grades.size()) * 100.0) / 100.0;
    }

    public double getGPA() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0.0;
        for (SubjectGrade g : grades) sum += g.getGradePoint();
        return Math.round((sum / grades.size()) * 100.0) / 100.0;
    }

    public String getOverallLetterGrade() {
        if (grades.isEmpty()) return "N/A";
        double avg = getAverageScore();
        if (avg >= 90.0) return "A";
        if (avg >= 80.0) return "B";
        if (avg >= 70.0) return "C";
        if (avg >= 60.0) return "D";
        return "F";
    }

    public SubjectGrade getHighestSubject() {
        if (grades.isEmpty()) return null;
        SubjectGrade highest = grades.get(0);
        for (SubjectGrade g : grades) {
            if (g.getScore() > highest.getScore()) highest = g;
        }
        return highest;
    }

    public SubjectGrade getLowestSubject() {
        if (grades.isEmpty()) return null;
        SubjectGrade lowest = grades.get(0);
        for (SubjectGrade g : grades) {
            if (g.getScore() < lowest.getScore()) lowest = g;
        }
        return lowest;
    }

    public boolean isPassed() {
        if (grades.isEmpty()) return false;
        return getAverageScore() >= 60.0;
    }

    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, Avg=%.2f, GPA=%.2f, Grade=%s]",
                id, name, getAverageScore(), getGPA(), getOverallLetterGrade());
    }
}
