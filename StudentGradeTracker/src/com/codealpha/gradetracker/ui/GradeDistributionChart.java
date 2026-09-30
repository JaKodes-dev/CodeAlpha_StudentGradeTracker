package com.codealpha.gradetracker.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GradeDistributionChart extends JPanel {
    private Map<String, Integer> distribution;
    private int totalStudents = 0;

    private static final Map<String, Color> GRADE_COLORS = Map.of(
            "A", new Color(46, 204, 113),
            "B", new Color(52, 152, 219),
            "C", new Color(241, 196, 15),
            "D", new Color(230, 126, 34),
            "F", new Color(231, 76, 60)
    );

    public GradeDistributionChart() {
        setPreferredSize(new Dimension(320, 220));
        setBackground(new Color(250, 252, 255));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 235), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
    }

    public void updateData(Map<String, Integer> distribution, int totalStudents) {
        this.distribution = distribution;
        this.totalStudents = totalStudents;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(new Color(44, 62, 80));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("Grade Distribution Analytics", 14, 22);

        if (distribution == null || distribution.isEmpty() || totalStudents == 0) {
            g2.setColor(new Color(150, 160, 175));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("No grade data available", width / 2 - 60, height / 2);
            g2.dispose();
            return;
        }

        String[] grades = {"A", "B", "C", "D", "F"};
        int maxVal = 1;
        for (String grade : grades) {
            int val = distribution.getOrDefault(grade, 0);
            if (val > maxVal) maxVal = val;
        }

        int chartLeft = 30;
        int chartRight = width - 20;
        int chartTop = 45;
        int chartBottom = height - 40;
        int chartHeight = chartBottom - chartTop;
        int barSlotWidth = (chartRight - chartLeft) / grades.length;
        int barWidth = Math.max(22, barSlotWidth - 24);

        g2.setColor(new Color(220, 226, 235));
        g2.drawLine(chartLeft, chartBottom, chartRight, chartBottom);

        for (int i = 0; i < grades.length; i++) {
            String grade = grades[i];
            int count = distribution.getOrDefault(grade, 0);
            double pct = (double) count / totalStudents * 100.0;

            int barH = (int) (((double) count / maxVal) * (chartHeight - 15));
            int x = chartLeft + i * barSlotWidth + (barSlotWidth - barWidth) / 2;
            int y = chartBottom - barH;

            Color baseColor = GRADE_COLORS.getOrDefault(grade, new Color(52, 152, 219));
            GradientPaint gp = new GradientPaint(x, y, baseColor, x, chartBottom, baseColor.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barWidth, barH, 6, 6);

            g2.setColor(new Color(50, 60, 75));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(grade, x + barWidth / 2 - 4, chartBottom + 16);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(100, 110, 125));
            String countLabel = String.valueOf(count);
            g2.drawString(countLabel, x + barWidth / 2 - (countLabel.length() * 3), y - 4);
            g2.drawString(String.format("%.0f%%", pct), x + barWidth / 2 - 8, chartBottom + 30);
        }

        g2.dispose();
    }
}
