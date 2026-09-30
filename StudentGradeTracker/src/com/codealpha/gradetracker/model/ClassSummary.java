package com.codealpha.gradetracker.model;

import java.util.HashMap;
import java.util.Map;

public class ClassSummary {
    private final int totalStudents;
    private final double classAverageScore;
    private final double classAverageGPA;
    private final Student highestScoringStudent;
    private final Student lowestScoringStudent;
    private final double highestScore;
    private final double lowestScore;
    private final Map<String, Integer> gradeDistribution;
    private final double passRate;

    public ClassSummary(int totalStudents, double classAverageScore, double classAverageGPA,
                        Student highestScoringStudent, Student lowestScoringStudent,
                        double highestScore, double lowestScore,
                        Map<String, Integer> gradeDistribution, double passRate) {
        this.totalStudents = totalStudents;
        this.classAverageScore = Math.round(classAverageScore * 100.0) / 100.0;
        this.classAverageGPA = Math.round(classAverageGPA * 100.0) / 100.0;
        this.highestScoringStudent = highestScoringStudent;
        this.lowestScoringStudent = lowestScoringStudent;
        this.highestScore = Math.round(highestScore * 100.0) / 100.0;
        this.lowestScore = Math.round(lowestScore * 100.0) / 100.0;
        this.gradeDistribution = gradeDistribution != null ? gradeDistribution : new HashMap<>();
        this.passRate = Math.round(passRate * 100.0) / 100.0;
    }

    public int getTotalStudents() { return totalStudents; }
    public double getClassAverageScore() { return classAverageScore; }
    public double getClassAverageGPA() { return classAverageGPA; }
    public Student getHighestScoringStudent() { return highestScoringStudent; }
    public Student getLowestScoringStudent() { return lowestScoringStudent; }
    public double getHighestScore() { return highestScore; }
    public double getLowestScore() { return lowestScore; }
    public Map<String, Integer> getGradeDistribution() { return gradeDistribution; }
    public double getPassRate() { return passRate; }
}
