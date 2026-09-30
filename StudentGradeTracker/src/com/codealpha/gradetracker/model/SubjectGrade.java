package com.codealpha.gradetracker.model;

import java.io.Serializable;

public class SubjectGrade implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subjectName;
    private double score;

    public SubjectGrade(String subjectName, double score) {
        if (score < 0.0 || score > 100.0) {
            throw new IllegalArgumentException("Score must be between 0.0 and 100.0. Provided: " + score);
        }
        this.subjectName = subjectName != null && !subjectName.trim().isEmpty() ? subjectName.trim() : "General Subject";
        this.score = Math.round(score * 100.0) / 100.0;
    }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName != null && !subjectName.trim().isEmpty() ? subjectName.trim() : "General Subject";
    }

    public double getScore() { return score; }
    public void setScore(double score) {
        if (score < 0.0 || score > 100.0) throw new IllegalArgumentException("Score must be between 0.0 and 100.0");
        this.score = Math.round(score * 100.0) / 100.0;
    }

    public String getLetterGrade() {
        if (score >= 90.0) return "A";
        if (score >= 80.0) return "B";
        if (score >= 70.0) return "C";
        if (score >= 60.0) return "D";
        return "F";
    }

    public double getGradePoint() {
        if (score >= 90.0) return 4.0;
        if (score >= 80.0) return 3.0;
        if (score >= 70.0) return 2.0;
        if (score >= 60.0) return 1.0;
        return 0.0;
    }

    public boolean isPassed() { return score >= 60.0; }

    @Override
    public String toString() {
        return String.format("%s: %.1f (%s, GP: %.1f)", subjectName, score, getLetterGrade(), getGradePoint());
    }
}
