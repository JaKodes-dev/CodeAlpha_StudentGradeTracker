package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.model.ClassSummary;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.model.SubjectGrade;
import com.codealpha.gradetracker.service.GradeTrackerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StudentGradeTrackerGUI extends JFrame {
    private final GradeTrackerService service;

    private JLabel totalStudentsLabel;
    private JLabel classAvgLabel;
    private JLabel classGpaLabel;
    private JLabel passRateLabel;
    private JLabel topScorerLabel;

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> gradeFilterCombo;
    private GradeDistributionChart chartPanel;
    private JTextArea studentDetailsArea;

    public StudentGradeTrackerGUI(GradeTrackerService service) {
        this.service = service != null ? service : new GradeTrackerService();
        initUI();
        refreshAllData();
    }

    private void initUI() {
        setTitle("CodeAlpha - Student Grade Tracker (Task 1)");
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBackground(new Color(245, 247, 250));
        rootPanel.setBorder(new EmptyBorder(12, 14, 12, 14));
        setContentPane(rootPanel);

        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createTablePanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(6);
        splitPane.setBackground(new Color(245, 247, 250));
        splitPane.setBorder(null);
        rootPanel.add(splitPane, BorderLayout.CENTER);

        rootPanel.add(createBottomToolbar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Bot Student Grade Tracker & Performance Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 41, 59));

        JLabel subtitle = new JLabel("CodeAlpha Java Programming Internship Portfolio Task 1 ? Comprehensive Academic Grading System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(100, 116, 139));
        titleBox.add(title);
        titleBox.add(subtitle);
        panel.add(titleBox, BorderLayout.NORTH);

        JPanel statRow = new JPanel(new GridLayout(1, 5, 10, 0));
        statRow.setOpaque(false);
        statRow.setBorder(new EmptyBorder(8, 0, 4, 0));

        totalStudentsLabel = new JLabel("0", SwingConstants.CENTER);
        classAvgLabel = new JLabel("0.0%", SwingConstants.CENTER);
        classGpaLabel = new JLabel("0.00", SwingConstants.CENTER);
        passRateLabel = new JLabel("0.0%", SwingConstants.CENTER);
        topScorerLabel = new JLabel("None", SwingConstants.CENTER);

        statRow.add(createCard("Total Students", totalStudentsLabel, new Color(59, 130, 246)));
        statRow.add(createCard("Class Average", classAvgLabel, new Color(16, 185, 129)));
        statRow.add(createCard("Class GPA", classGpaLabel, new Color(139, 92, 246)));
        statRow.add(createCard("Pass Rate", passRateLabel, new Color(245, 158, 11)));
        statRow.add(createCard("Top Performer", topScorerLabel, new Color(236, 72, 153)));

        panel.add(statRow, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        valueLabel.setForeground(accent);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.addCaretListener(e -> filterTable());

        JPanel filterBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        filterBox.setOpaque(false);
        filterBox.add(new JLabel("Grade Filter:"));
        gradeFilterCombo = new JComboBox<>(new String[]{"ALL", "A", "B", "C", "D", "F"});
        gradeFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gradeFilterCombo.addActionListener(e -> filterTable());
        filterBox.add(gradeFilterCombo);

        searchBar.add(new JLabel("Bot Search:"), BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(filterBox, BorderLayout.EAST);
        panel.add(searchBar, BorderLayout.NORTH);

        String[] columns = {"ID", "Student Name", "Subjects", "Avg Score", "GPA", "Letter Grade", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(28);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(241, 245, 249));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setShowVerticalLines(false);
        studentTable.setGridColor(new Color(235, 240, 245));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedStudentView();
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setOpaque(false);

        chartPanel = new GradeDistributionChart();
        panel.add(chartPanel);

        JPanel detailsPanel = new JPanel(new BorderLayout(6, 6));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel title = new JLabel("Bot Student Report Card & Subjects");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(30, 41, 59));
        detailsPanel.add(title, BorderLayout.NORTH);

        studentDetailsArea = new JTextArea("Select a student from the table to view subject breakdowns and report card.");
        studentDetailsArea.setEditable(false);
        studentDetailsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        studentDetailsArea.setBackground(new Color(248, 250, 252));
        studentDetailsArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JScrollPane scroll = new JScrollPane(studentDetailsArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        detailsPanel.add(scroll, BorderLayout.CENTER);

        panel.add(detailsPanel);
        return panel;
    }

    private JPanel createBottomToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setOpaque(false);

        JButton addBtn = createStyledButton("Error: Add Student", new Color(37, 99, 235));
        addBtn.addActionListener(e -> showAddStudentDialog());

        JButton editGradesBtn = createStyledButton("Bot Manage Grades", new Color(13, 148, 136));
        editGradesBtn.addActionListener(e -> showManageGradesDialog());

        JButton deleteBtn = createStyledButton("->Bot Delete Student", new Color(220, 38, 38));
        deleteBtn.addActionListener(e -> deleteSelectedStudent());

        JButton sampleBtn = createStyledButton("Bot Reset / Sample Data", new Color(100, 116, 139));
        sampleBtn.addActionListener(e -> {
            service.seedSampleData();
            refreshAllData();
            JOptionPane.showMessageDialog(this, "Sample benchmark dataset loaded successfully!", "Data Loaded", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton exportBtn = createStyledButton("Bot Export CSV", new Color(15, 118, 110));
        exportBtn.addActionListener(e -> exportData());

        JButton importBtn = createStyledButton("Bot Import CSV", new Color(79, 70, 229));
        importBtn.addActionListener(e -> importData());

        toolbar.add(addBtn);
        toolbar.add(editGradesBtn);
        toolbar.add(deleteBtn);
        toolbar.add(exportBtn);
        toolbar.add(importBtn);
        toolbar.add(sampleBtn);

        return toolbar;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void filterTable() {
        String query = searchField.getText();
        String grade = (String) gradeFilterCombo.getSelectedItem();
        List<Student> filtered = service.search(query, grade);
        renderTableRows(filtered);
    }

    private void refreshAllData() {
        filterTable();

        ClassSummary summary = service.calculateClassSummary();
        totalStudentsLabel.setText(String.valueOf(summary.getTotalStudents()));
        classAvgLabel.setText(String.format("%.1f%%", summary.getClassAverageScore()));
        classGpaLabel.setText(String.format("%.2f", summary.getClassAverageGPA()));
        passRateLabel.setText(String.format("%.1f%%", summary.getPassRate()));

        if (summary.getHighestScoringStudent() != null) {
            topScorerLabel.setText(summary.getHighestScoringStudent().getName() + " (" + summary.getHighestScore() + "%)");
        } else {
            topScorerLabel.setText("N/A");
        }

        chartPanel.updateData(summary.getGradeDistribution(), summary.getTotalStudents());
        updateSelectedStudentView();
    }

    private void renderTableRows(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getGrades().size(),
                    String.format("%.1f", s.getAverageScore()),
                    String.format("%.2f", s.getGPA()),
                    s.getOverallLetterGrade(),
                    s.isPassed() ? "PASSED" : "FAILED"
            });
        }
    }

    private Student getSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) return null;
        String id = (String) tableModel.getValueAt(row, 0);
        return service.findById(id).orElse(null);
    }

    private void updateSelectedStudentView() {
        Student s = getSelectedStudent();
        if (s == null) {
            studentDetailsArea.setText("Select a student from the table to view subject breakdowns and report card.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("================ REPORT CARD ================\n");
        sb.append(String.format("Student ID:    %s\n", s.getId()));
        sb.append(String.format("Name:          %s\n", s.getName()));
        sb.append(String.format("Email:         %s\n", s.getEmail().isEmpty() ? "N/A" : s.getEmail()));
        sb.append(String.format("Average Score: %.2f / 100.0 (%s)\n", s.getAverageScore(), s.getOverallLetterGrade()));
        sb.append(String.format("Cumulative GPA:%.2f / 4.0\n", s.getGPA()));
        sb.append(String.format("Academic Status: %s\n", s.isPassed() ? "GOOD STANDING (PASSED)" : "ACADEMIC WARNING (FAILED)"));
        sb.append("---------------------------------------------\n");
        sb.append("SUBJECT SCORES & GRADE POINTS:\n");
        if (s.getGrades().isEmpty()) {
            sb.append("  (No subject grades recorded yet)\n");
        } else {
            for (SubjectGrade g : s.getGrades()) {
                sb.append(String.format("  ? %-20s : %5.1f%% | Grade %-2s | GP: %.1f\n",
                        g.getSubjectName(), g.getScore(), g.getLetterGrade(), g.getGradePoint()));
            }
            if (s.getHighestSubject() != null) {
                sb.append(String.format("\nHighest Subject: %s (%.1f%%)\n", s.getHighestSubject().getSubjectName(), s.getHighestSubject().getScore()));
            }
            if (s.getLowestSubject() != null) {
                sb.append(String.format("Lowest Subject:  %s (%.1f%%)\n", s.getLowestSubject().getSubjectName(), s.getLowestSubject().getScore()));
            }
        }
        sb.append("=============================================");
        studentDetailsArea.setText(sb.toString());
        studentDetailsArea.setCaretPosition(0);
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add New Student", true);
        dialog.setSize(380, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(new EmptyBorder(16, 16, 16, 16));

        JTextField idField = new JTextField("STU-" + (int)(100 + Math.random() * 900));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();

        form.add(new JLabel("Student ID:"));
        form.add(idField);
        form.add(new JLabel("Student Name:"));
        form.add(nameField);
        form.add(new JLabel("Email Address:"));
        form.add(emailField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createStyledButton("Save Student", new Color(37, 99, 235));
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Student ID and Name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student s = new Student(id, name, email);
            if (service.addStudent(s)) {
                dialog.dispose();
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Student added successfully! Now manage their subject grades.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "A student with this ID already exists.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showManageGradesDialog() {
        Student s = getSelectedStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Manage Grades: " + s.getName() + " (" + s.getId() + ")", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (SubjectGrade g : s.getGrades()) {
            listModel.addElement(g.toString());
        }

        JList<String> gradeList = new JList<>(listModel);
        gradeList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(gradeList);
        scroll.setBorder(BorderFactory.createTitledBorder("Existing Subject Grades"));
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add / Update Subject Score"));
        JTextField subjectField = new JTextField();
        JTextField scoreField = new JTextField();

        addPanel.add(new JLabel("Subject Name:"));
        addPanel.add(subjectField);
        addPanel.add(new JLabel("Score (0.0 - 100.0):"));
        addPanel.add(scoreField);

        JButton addGradeBtn = createStyledButton("Add / Update Grade", new Color(13, 148, 136));
        addGradeBtn.addActionListener(e -> {
            String subject = subjectField.getText().trim();
            String scoreStr = scoreField.getText().trim();
            if (subject.isEmpty() || scoreStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Subject and score are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double score = Double.parseDouble(scoreStr);
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(dialog, "Score must be between 0 and 100.", "Invalid Range", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                s.addGrade(new SubjectGrade(subject, score));
                service.updateStudent(s);

                listModel.clear();
                for (SubjectGrade g : s.getGrades()) {
                    listModel.addElement(g.toString());
                }
                subjectField.setText("");
                scoreField.setText("");
                refreshAllData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid numeric score (e.g. 85.5).", "Number Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton deleteGradeBtn = new JButton("Remove Selected Subject");
        deleteGradeBtn.addActionListener(e -> {
            int idx = gradeList.getSelectedIndex();
            if (idx != -1 && idx < s.getGrades().size()) {
                SubjectGrade g = s.getGrades().get(idx);
                s.removeGrade(g.getSubjectName());
                service.updateStudent(s);
                listModel.remove(idx);
                refreshAllData();
            }
        });

        addPanel.add(deleteGradeBtn);
        addPanel.add(addGradeBtn);

        dialog.add(addPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedStudent() {
        Student s = getSelectedStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + s.getName() + " (" + s.getId() + ")->",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            service.deleteStudent(s.getId());
            refreshAllData();
        }
    }

    private void exportData() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("students_export.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                service.exportToCSV(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Data exported successfully to CSV!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importData() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int count = service.importFromCSV(chooser.getSelectedFile());
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Imported " + count + " student records successfully!", "Import Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error importing CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            String status = value != null ? value.toString() : "";
            if ("PASSED".equalsIgnoreCase(status)) {
                setForeground(new Color(22, 163, 74));
            } else {
                setForeground(new Color(220, 38, 38));
            }
            return c;
        }
    }
}
