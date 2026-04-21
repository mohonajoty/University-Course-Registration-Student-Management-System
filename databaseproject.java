// ==============================================
// UNIVERSITY MANAGEMENT SYSTEM - COMPLETE PROJECT
// SINGLE FILE VERSION
// ==============================================

package universitymanagementsystem1;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

// ==============================================
// 1. DBConnection Class
// ==============================================
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException e) {
                System.out.println("MySQL JDBC Driver not found!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Connection failed!");
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static ResultSet executeQuery(String query) {
        try {
            Statement stmt = getConnection().createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static int executeUpdate(String query) {
        try {
            Statement stmt = getConnection().createStatement();
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}

// ==============================================
// 2. UserSessionManager Class
// ==============================================
class UserSessionManager {
    private static int userId;
    private static String userType;
    private static String userName;
    
    public static void setUser(int id, String type, String name) {
        userId = id;
        userType = type;
        userName = name;
    }
    
    public static int getUserId() { return userId; }
    public static String getUserType() { return userType; }
    public static String getUserName() { return userName; }
    
    public static boolean isAdmin() { return "ADMIN".equals(userType); }
    public static boolean isFaculty() { return "FACULTY".equals(userType); }
    public static boolean isStudent() { return "STUDENT".equals(userType); }
    
    public static void logout() {
        userId = 0;
        userType = null;
        userName = null;
    }
}

// ==============================================
// 3. Course Model Class
// ==============================================
class Course {
    private int courseId;
    private String courseName;
    private int credits;
    private int departmentId;
    
    public Course() {}
    
    public Course(String courseName, int credits, int departmentId) {
        this.courseName = courseName;
        this.credits = credits;
        this.departmentId = departmentId;
    }
    
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
}

// ==============================================
// 4. Department Model Class
// ==============================================
class Department {
    private int departmentId;
    private String departmentName;
    
    public Department() {}
    public Department(String departmentName) { this.departmentName = departmentName; }
    
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}

// ==============================================
// 5. Faculty Model Class
// ==============================================
class Faculty {
    private int facultyId;
    private String name;
    private String email;
    private int departmentId;
    
    public Faculty() {}
    public Faculty(String name, String email, int departmentId) {
        this.name = name;
        this.email = email;
        this.departmentId = departmentId;
    }
    
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
}

// ==============================================
// 6. Student Model Class
// ==============================================
class Student {
    private int studentId;
    private String name;
    private String email;
    private String phone;
    private int departmentId;
    
    public Student() {}
    public Student(String name, String email, String phone, int departmentId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.departmentId = departmentId;
    }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
}

// ==============================================
// 7. CourseManagementFrame Class
// ==============================================
class CourseManagementFrame extends JFrame {
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, creditsField;
    private JComboBox<String> deptCombo;
    private int selectedCourseId = -1;
    
    public CourseManagementFrame() {
        setTitle("Manage Courses");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Information"));
        
        formPanel.add(new JLabel("Course Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Credits:"));
        creditsField = new JTextField();
        formPanel.add(creditsField);
        
        formPanel.add(new JLabel("Department:"));
        deptCombo = new JComboBox<>();
        loadDepartments();
        formPanel.add(deptCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        
        addButton.addActionListener(e -> addCourse());
        updateButton.addActionListener(e -> updateCourse());
        deleteButton.addActionListener(e -> deleteCourse());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        String[] columns = {"ID", "Course Name", "Credits", "Department"};
        tableModel = new DefaultTableModel(columns, 0);
        courseTable = new JTable(tableModel);
        courseTable.getSelectionModel().addListSelectionListener(e -> loadSelectedCourse());
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadCourses();
    }
    
    private void loadDepartments() {
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT department_id, department_name FROM department");
            while (rs.next()) {
                deptCombo.addItem(rs.getInt("department_id") + " - " + rs.getString("department_name"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadCourses() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT c.*, d.department_name FROM course c " +
                          "LEFT JOIN department d ON c.department_id = d.department_id";
            ResultSet rs = DBConnection.executeQuery(query);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getInt("credits"),
                    rs.getString("department_name")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addCourse() {
        String courseIdStr = JOptionPane.showInputDialog(this, "Enter Course ID (e.g., 201):");
        String name = nameField.getText();
        String credits = creditsField.getText();
        String deptSelected = (String) deptCombo.getSelectedItem();
        int deptId = Integer.parseInt(deptSelected.split(" - ")[0]);
        
        if (courseIdStr == null || courseIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course ID is required!");
            return;
        }
        
        int courseId = Integer.parseInt(courseIdStr);
        
        if (name.isEmpty() || credits.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }
        
        String checkQuery = "SELECT course_id FROM course WHERE course_id = " + courseId;
        ResultSet rs = DBConnection.executeQuery(checkQuery);
        try {
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Course ID already exists! Use a different ID.");
                rs.close();
                return;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String query = String.format("INSERT INTO course (course_id, course_name, credits, department_id) VALUES (%d, '%s', %s, %d)",
                  courseId, name, credits, deptId);
        
        if (DBConnection.executeUpdate(query) > 0) {
            JOptionPane.showMessageDialog(this, "Course added successfully! ID: " + courseId);
            loadCourses();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add course!");
        }
    }
    
    private void updateCourse() {
        if (selectedCourseId == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to update!");
            return;
        }
        
        String name = nameField.getText();
        String credits = creditsField.getText();
        String deptSelected = (String) deptCombo.getSelectedItem();
        int deptId = Integer.parseInt(deptSelected.split(" - ")[0]);
        
        String query = String.format("UPDATE course SET course_name='%s', credits=%s, department_id=%d WHERE course_id=%d",
                      name, credits, deptId, selectedCourseId);
        
        if (DBConnection.executeUpdate(query) > 0) {
            JOptionPane.showMessageDialog(this, "Course updated successfully!");
            loadCourses();
            clearForm();
        }
    }
    
    private void deleteCourse() {
        if (selectedCourseId == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this course?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (DBConnection.executeUpdate("DELETE FROM course WHERE course_id=" + selectedCourseId) > 0) {
                JOptionPane.showMessageDialog(this, "Course deleted!");
                loadCourses();
                clearForm();
            }
        }
    }
    
    private void loadSelectedCourse() {
        int row = courseTable.getSelectedRow();
        if (row >= 0) {
            selectedCourseId = (int) tableModel.getValueAt(row, 0);
            nameField.setText((String) tableModel.getValueAt(row, 1));
            creditsField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
            
            String deptName = (String) tableModel.getValueAt(row, 3);
            for (int i = 0; i < deptCombo.getItemCount(); i++) {
                if (deptCombo.getItemAt(i).contains(deptName)) {
                    deptCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void clearForm() {
        selectedCourseId = -1;
        nameField.setText("");
        creditsField.setText("");
        deptCombo.setSelectedIndex(0);
        courseTable.clearSelection();
    }
}

// ==============================================
// 8. CourseRegistrationFrame Class
// ==============================================
class CourseRegistrationFrame extends JFrame {
    private JComboBox<String> studentCombo;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    
    public CourseRegistrationFrame() {
        setTitle("Course Registration - Admin Panel");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Registration Information"));
        
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Select Student:"));
        studentCombo = new JComboBox<>();
        studentCombo.setPreferredSize(new Dimension(250, 30));
        loadStudents();
        selectionPanel.add(studentCombo);
        
        JButton registerButton = new JButton("REGISTER SELECTED COURSES");
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(250, 40));
        registerButton.addActionListener(e -> registerCourse());
        
        topPanel.add(selectionPanel, BorderLayout.WEST);
        topPanel.add(registerButton, BorderLayout.EAST);
        
        String[] columns = {"Select", "Course ID", "Course Name", "Credits", "Department"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        
        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(30);
        loadCourses();
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        JLabel instructionLabel = new JLabel("  ✓ Select a student, check courses, then click 'REGISTER SELECTED COURSES'");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionLabel.setForeground(new Color(0, 102, 51));
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(instructionLabel, BorderLayout.SOUTH);
    }
    
    private void loadStudents() {
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT student_id, name FROM student WHERE status = 'active' ORDER BY student_id");
            studentCombo.addItem("-- Select Student --");
            while (rs.next()) {
                studentCombo.addItem(rs.getInt("student_id") + " - " + rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadCourses() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT c.course_id, c.course_name, c.credits, d.department_name " +
                          "FROM course c LEFT JOIN department d ON c.department_id = d.department_id " +
                          "ORDER BY c.course_id";
            ResultSet rs = DBConnection.executeQuery(query);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    false,
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getInt("credits"),
                    rs.getString("department_name")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void registerCourse() {
        String studentSelected = (String) studentCombo.getSelectedItem();
        
        if (studentSelected == null || studentSelected.equals("-- Select Student --")) {
            JOptionPane.showMessageDialog(this, "Please select a student!");
            return;
        }
        
        int studentId = Integer.parseInt(studentSelected.split(" - ")[0]);
        String studentName = studentSelected.split(" - ")[1];
        
        java.util.ArrayList<String> successList = new java.util.ArrayList<>();
        java.util.ArrayList<String> failList = new java.util.ArrayList<>();
        
        for (int i = 0; i < courseTable.getRowCount(); i++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
            
            if (isSelected != null && isSelected) {
                int courseId = (int) tableModel.getValueAt(i, 1);
                String courseName = (String) tableModel.getValueAt(i, 2);
                
                String checkQuery = "SELECT * FROM registration WHERE student_id = " + studentId + " AND course_id = " + courseId;
                try {
                    ResultSet rs = DBConnection.executeQuery(checkQuery);
                    if (rs.next()) {
                        failList.add(courseName + " (Already registered)");
                        rs.close();
                        continue;
                    }
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                
                String insertQuery = "INSERT INTO registration (student_id, course_id, grade) VALUES (" + studentId + ", " + courseId + ", 'Pending')";
                int result = DBConnection.executeUpdate(insertQuery);
                
                if (result > 0) {
                    successList.add(courseName);
                } else {
                    failList.add(courseName + " (Database error)");
                }
            }
        }
        
        StringBuilder message = new StringBuilder();
        message.append("Student: ").append(studentName).append(" (ID: ").append(studentId).append(")\n\n");
        
        if (!successList.isEmpty()) {
            message.append("✅ SUCCESSFULLY REGISTERED:\n");
            for (String course : successList) {
                message.append("   • ").append(course).append("\n");
            }
        }
        
        if (!failList.isEmpty()) {
            if (!successList.isEmpty()) message.append("\n");
            message.append("❌ FAILED TO REGISTER:\n");
            for (String course : failList) {
                message.append("   • ").append(course).append("\n");
            }
        }
        
        if (!successList.isEmpty() || !failList.isEmpty()) {
            JOptionPane.showMessageDialog(this, message.toString());
            if (!successList.isEmpty()) {
                loadCourses();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No courses selected! Please check the boxes next to courses to register.");
        }
    }
}

// ==============================================
// 9. DashboardFrame Class
// ==============================================
class DashboardFrame extends JFrame {
    
    public DashboardFrame() {
        if (!UserSessionManager.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Access Denied! Admin only.");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }
        
        setTitle("Admin Dashboard - Welcome " + UserSessionManager.getUserName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu studentMenu = new JMenu("Students");
        JMenuItem addStudentItem = new JMenuItem("Manage Students");
        addStudentItem.addActionListener(e -> new StudentManagementFrame().setVisible(true));
        studentMenu.add(addStudentItem);
        
        JMenu courseMenu = new JMenu("Courses");
        JMenuItem manageCourseItem = new JMenuItem("Manage Courses");
        manageCourseItem.addActionListener(e -> new CourseManagementFrame().setVisible(true));
        courseMenu.add(manageCourseItem);
        
        JMenu facultyMenu = new JMenu("Faculty");
        JMenuItem manageFacultyItem = new JMenuItem("Manage Faculty");
        manageFacultyItem.addActionListener(e -> new FacultyManagementFrame().setVisible(true));
        facultyMenu.add(manageFacultyItem);
        
        JMenu deptMenu = new JMenu("Departments");
        JMenuItem manageDeptItem = new JMenuItem("Manage Departments");
        manageDeptItem.addActionListener(e -> new DepartmentManagementFrame().setVisible(true));
        deptMenu.add(manageDeptItem);
        
        JMenu regMenu = new JMenu("Registration");
        JMenuItem registerCourseItem = new JMenuItem("Course Registration");
        registerCourseItem.addActionListener(e -> new CourseRegistrationFrame().setVisible(true));
        JMenuItem viewRegistrationItem = new JMenuItem("View Registrations");
        viewRegistrationItem.addActionListener(e -> new ViewRegistrationsFrame().setVisible(true));
        regMenu.add(registerCourseItem);
        regMenu.add(viewRegistrationItem);
        
        JMenu gradeMenu = new JMenu("Grades");
        JMenuItem viewGradesItem = new JMenuItem("View All Grades");
        viewGradesItem.addActionListener(e -> new ViewRegistrationsFrame().setVisible(true));
        gradeMenu.add(viewGradesItem);
        
        JMenu scheduleMenu = new JMenu("Schedule");
        JMenuItem viewScheduleItem = new JMenuItem("View Class Schedule");
        viewScheduleItem.addActionListener(e -> new ViewScheduleFrame().setVisible(true));
        scheduleMenu.add(viewScheduleItem);
        
        menuBar.add(studentMenu);
        menuBar.add(courseMenu);
        menuBar.add(facultyMenu);
        menuBar.add(deptMenu);
        menuBar.add(regMenu);
        menuBar.add(gradeMenu);
        menuBar.add(scheduleMenu);
        
        JMenu systemMenu = new JMenu("System");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                           "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserSessionManager.logout();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        systemMenu.add(logoutItem);
        menuBar.add(systemMenu);
        
        setJMenuBar(menuBar);
        
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(240, 248, 255));
        JLabel welcomeLabel = new JLabel("Welcome to University Management System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 51, 102));
        welcomePanel.add(welcomeLabel);
        
        JLabel infoLabel = new JLabel("Use the menu above to manage students, courses, faculty, and registrations", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomePanel.add(infoLabel);
        
        add(welcomePanel);
    }
}

// ==============================================
// 10. DepartmentManagementFrame Class
// ==============================================
class DepartmentManagementFrame extends JFrame {
    private JTable deptTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private int selectedDeptId = -1;
    
    public DepartmentManagementFrame() {
        setTitle("Manage Departments");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Information"));
        
        formPanel.add(new JLabel("Department Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        
        addButton.addActionListener(e -> addDepartment());
        updateButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        String[] columns = {"ID", "Department Name"};
        tableModel = new DefaultTableModel(columns, 0);
        deptTable = new JTable(tableModel);
        deptTable.getSelectionModel().addListSelectionListener(e -> loadSelectedDepartment());
        
        JScrollPane scrollPane = new JScrollPane(deptTable);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadDepartments();
    }
    
    private void loadDepartments() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT * FROM department");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("department_id"),
                    rs.getString("department_name")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addDepartment() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter department name!");
            return;
        }
        
        String query = "INSERT INTO department (department_name) VALUES ('" + name + "')";
        if (DBConnection.executeUpdate(query) > 0) {
            JOptionPane.showMessageDialog(this, "Department added successfully!");
            loadDepartments();
            clearForm();
        }
    }
    
    private void updateDepartment() {
        if (selectedDeptId == -1) {
            JOptionPane.showMessageDialog(this, "Select a department to update!");
            return;
        }
        
        String name = nameField.getText();
        String query = "UPDATE department SET department_name='" + name + "' WHERE department_id=" + selectedDeptId;
        
        if (DBConnection.executeUpdate(query) > 0) {
            JOptionPane.showMessageDialog(this, "Department updated successfully!");
            loadDepartments();
            clearForm();
        }
    }
    
    private void deleteDepartment() {
        if (selectedDeptId == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this department?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (DBConnection.executeUpdate("DELETE FROM department WHERE department_id=" + selectedDeptId) > 0) {
                JOptionPane.showMessageDialog(this, "Department deleted!");
                loadDepartments();
                clearForm();
            }
        }
    }
    
    private void loadSelectedDepartment() {
        int row = deptTable.getSelectedRow();
        if (row >= 0) {
            selectedDeptId = (int) tableModel.getValueAt(row, 0);
            nameField.setText((String) tableModel.getValueAt(row, 1));
        }
    }
    
    private void clearForm() {
        selectedDeptId = -1;
        nameField.setText("");
        deptTable.clearSelection();
    }
}

// ==============================================
// 11. FacultyDashboardFrame Class
// ==============================================
class FacultyDashboardFrame extends JFrame {
    
    public FacultyDashboardFrame() {
        setTitle("Faculty Dashboard - Welcome " + UserSessionManager.getUserName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gradeMenu = new JMenu("Grades");
        JMenuItem updateGradeItem = new JMenuItem("Update Student Grades");
        updateGradeItem.addActionListener(e -> new FacultyGradeUpdateFrame().setVisible(true));
        gradeMenu.add(updateGradeItem);
        
        JMenuItem viewStudentsItem = new JMenuItem("View My Students");
        viewStudentsItem.addActionListener(e -> new FacultyStudentViewFrame().setVisible(true));
        gradeMenu.add(viewStudentsItem);
        
        JMenu scheduleMenu = new JMenu("Schedule");
        JMenuItem viewScheduleItem = new JMenuItem("View Class Schedule");
        viewScheduleItem.addActionListener(e -> new ViewScheduleFrame().setVisible(true));
        scheduleMenu.add(viewScheduleItem);
        
        JMenu logoutMenu = new JMenu("System");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                           "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserSessionManager.logout();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        logoutMenu.add(logoutItem);
        
        menuBar.add(gradeMenu);
        menuBar.add(scheduleMenu);
        menuBar.add(logoutMenu);
        
        setJMenuBar(menuBar);
        
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(240, 248, 255));
        JLabel welcomeLabel = new JLabel("Welcome Professor " + UserSessionManager.getUserName() + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 51, 102));
        welcomePanel.add(welcomeLabel);
        
        JLabel infoLabel = new JLabel("You can update grades for your courses and view your schedule", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomePanel.add(infoLabel);
        
        add(welcomePanel);
    }
}

// ==============================================
// 12. FacultyGradeUpdateFrame Class
// ==============================================
class FacultyGradeUpdateFrame extends JFrame {
    private JComboBox<String> courseCombo;
    private JComboBox<String> studentCombo;
    private JComboBox<String> gradeCombo;
    private int facultyId;
    
    public FacultyGradeUpdateFrame() {
        facultyId = UserSessionManager.getUserId();
        
        setTitle("Update Student Grades - " + UserSessionManager.getUserName());
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));
        
        add(new JLabel("Select Course:"));
        courseCombo = new JComboBox<>();
        loadFacultyCourses();
        courseCombo.addActionListener(e -> loadStudentsForCourse());
        add(courseCombo);
        
        add(new JLabel("Select Student:"));
        studentCombo = new JComboBox<>();
        add(studentCombo);
        
        add(new JLabel("Select Grade:"));
        gradeCombo = new JComboBox<>(new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"});
        add(gradeCombo);
        
        JButton updateButton = new JButton("Update Grade");
        updateButton.addActionListener(e -> updateGrade());
        add(updateButton);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton);
    }
    
    private void loadFacultyCourses() {
        try {
            String query = "SELECT c.course_id, c.course_name " +
                          "FROM course c " +
                          "JOIN class cl ON c.course_id = cl.course_id " +
                          "WHERE cl.faculty_id = " + facultyId;
            
            ResultSet rs = DBConnection.executeQuery(query);
            while (rs.next()) {
                courseCombo.addItem(rs.getInt("course_id") + " - " + rs.getString("course_name"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadStudentsForCourse() {
        studentCombo.removeAllItems();
        String selected = (String) courseCombo.getSelectedItem();
        if (selected == null) return;
        
        int courseId = Integer.parseInt(selected.split(" - ")[0]);
        
        try {
            String query = "SELECT s.student_id, s.name " +
                          "FROM registration r " +
                          "JOIN student s ON r.student_id = s.student_id " +
                          "WHERE r.course_id = " + courseId;
            
            ResultSet rs = DBConnection.executeQuery(query);
            while (rs.next()) {
                studentCombo.addItem(rs.getInt("student_id") + " - " + rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateGrade() {
        String courseSelected = (String) courseCombo.getSelectedItem();
        String studentSelected = (String) studentCombo.getSelectedItem();
        String newGrade = (String) gradeCombo.getSelectedItem();
        
        if (courseSelected == null || studentSelected == null) {
            JOptionPane.showMessageDialog(this, "Please select course and student!");
            return;
        }
        
        int courseId = Integer.parseInt(courseSelected.split(" - ")[0]);
        int studentId = Integer.parseInt(studentSelected.split(" - ")[0]);
        
        String query = String.format("UPDATE registration SET grade = '%s' WHERE student_id = %d AND course_id = %d",
                      newGrade, studentId, courseId);
        
        int result = DBConnection.executeUpdate(query);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Grade updated successfully to " + newGrade + "!");
        } else {
            JOptionPane.showMessageDialog(this, "Update failed!");
        }
    }
}

// ==============================================
// 13. FacultyManagementFrame Class
// ==============================================
class FacultyManagementFrame extends JFrame {
    private JTable facultyTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField;
    private JComboBox<String> deptCombo;
    private int selectedFacultyId = -1;
    
    public FacultyManagementFrame() {
        setTitle("Manage Faculty");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Faculty Information"));
        
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Department:"));
        deptCombo = new JComboBox<>();
        loadDepartments();
        formPanel.add(deptCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        
        addButton.addActionListener(e -> addFaculty());
        updateButton.addActionListener(e -> updateFaculty());
        deleteButton.addActionListener(e -> deleteFaculty());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        String[] columns = {"ID", "Name", "Email", "Department"};
        tableModel = new DefaultTableModel(columns, 0);
        facultyTable = new JTable(tableModel);
        facultyTable.getSelectionModel().addListSelectionListener(e -> loadSelectedFaculty());
        
        JScrollPane scrollPane = new JScrollPane(facultyTable);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadFaculty();
    }
    
    private void loadDepartments() {
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT department_id, department_name FROM department");
            while (rs.next()) {
                deptCombo.addItem(rs.getInt("department_id") + " - " + rs.getString("department_name"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadFaculty() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT f.*, d.department_name FROM faculty f " +
                          "LEFT JOIN department d ON f.department_id = d.department_id";
            ResultSet rs = DBConnection.executeQuery(query);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("faculty_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department_name")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addFaculty() {
        String facultyIdStr = JOptionPane.showInputDialog(this, "Enter Faculty ID (e.g., 101):");
        String name = nameField.getText();
        String email = emailField.getText();
        String deptSelected = (String) deptCombo.getSelectedItem();
        int deptId = Integer.parseInt(deptSelected.split(" - ")[0]);
        
        if (facultyIdStr == null || facultyIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Faculty ID is required!");
            return;
        }
        
        int facultyId = Integer.parseInt(facultyIdStr);
        
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required!");
            return;
        }
        
        String checkQuery = "SELECT faculty_id FROM faculty WHERE faculty_id = " + facultyId;
        ResultSet rs = DBConnection.executeQuery(checkQuery);
        try {
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Faculty ID already exists! Use a different ID.");
                rs.close();
                return;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String query = String.format("INSERT INTO faculty (faculty_id, name, email, phn_no, department_id, password, status) VALUES (%d, '%s', '%s', '%s', %d, 'faculty123', 'active')",
                  facultyId, name, email, "", deptId);
        
        if (DBConnection.executeUpdate(query) > 0) {
            JOptionPane.showMessageDialog(this, "Faculty added successfully! ID: " + facultyId);
            loadFaculty();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add faculty!");
        }
    }
    
    private void updateFaculty() {
        if (selectedFacultyId == -1) {
            JOptionPane.showMessageDialog(this, "Select a faculty member to update!");
            return;
        }
        
        String name = nameField.getText();
        String email = emailField.getText();
        String deptSelected = (String) deptCombo.getSelectedItem();
        int deptId = Integer.parseInt(deptSelected.split(" - ")[0]);
        
        String query = String.format("UPDATE faculty SET name='%s', email='%s', department_id=%d WHERE faculty_id=%d",
                      name, email, deptId, selectedFacultyId);
        
        if (DBConnection.executeUpdate(query) > 0) {
            JOptionPane.showMessageDialog(this, "Faculty updated successfully!");
            loadFaculty();
            clearForm();
        }
    }
    
    private void deleteFaculty() {
        if (selectedFacultyId == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this faculty member?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (DBConnection.executeUpdate("DELETE FROM faculty WHERE faculty_id=" + selectedFacultyId) > 0) {
                JOptionPane.showMessageDialog(this, "Faculty deleted!");
                loadFaculty();
                clearForm();
            }
        }
    }
    
    private void loadSelectedFaculty() {
        int row = facultyTable.getSelectedRow();
        if (row >= 0) {
            selectedFacultyId = (int) tableModel.getValueAt(row, 0);
            nameField.setText((String) tableModel.getValueAt(row, 1));
            emailField.setText((String) tableModel.getValueAt(row, 2));
            
            String deptName = (String) tableModel.getValueAt(row, 3);
            for (int i = 0; i < deptCombo.getItemCount(); i++) {
                if (deptCombo.getItemAt(i).contains(deptName)) {
                    deptCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void clearForm() {
        selectedFacultyId = -1;
        nameField.setText("");
        emailField.setText("");
        deptCombo.setSelectedIndex(0);
        facultyTable.clearSelection();
    }
}

// ==============================================
// 14. FacultyStudentViewFrame Class
// ==============================================
class FacultyStudentViewFrame extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    
    public FacultyStudentViewFrame() {
        setTitle("My Students - " + UserSessionManager.getUserName());
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        String[] columns = {"Student ID", "Student Name", "Email", "Phone", "Course", "Grade"};
        tableModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tableModel);
        
        loadMyStudents();
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane);
    }
    
    private void loadMyStudents() {
        int facultyId = UserSessionManager.getUserId();
        
        try {
            String query = "SELECT s.student_id, s.name, s.email, s.phone, c.course_name, r.grade " +
                          "FROM registration r " +
                          "JOIN student s ON r.student_id = s.student_id " +
                          "JOIN course c ON r.course_id = c.course_id " +
                          "JOIN class cl ON c.course_id = cl.course_id " +
                          "WHERE cl.faculty_id = " + facultyId +
                          " ORDER BY s.name";
            
            ResultSet rs = DBConnection.executeQuery(query);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("course_name"),
                    rs.getString("grade")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// ==============================================
// 15. LoginFrame Class (MAIN CLASS)
// ==============================================
class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    
    public LoginFrame() {
        setTitle("University Management System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titleLabel = new JLabel("University Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Login As:"), gbc);
        
        roleCombo = new JComboBox<>(new String[]{"ADMIN", "FACULTY", "STUDENT"});
        gbc.gridx = 1;
        mainPanel.add(roleCombo, gbc);
        
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Username/ID:"), gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> login());
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearFields());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private void login() {
        String role = (String) roleCombo.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username/ID and password!");
            return;
        }
        
        try {
            if (role.equals("ADMIN")) {
                String query = "SELECT admin_id, username FROM admin WHERE username = '" + username + 
                              "' AND password = '" + password + "'";
                ResultSet rs = DBConnection.executeQuery(query);
                
                if (rs.next()) {
                    UserSessionManager.setUser(rs.getInt("admin_id"), "ADMIN", rs.getString("username"));
                    JOptionPane.showMessageDialog(this, "Welcome Admin: " + username);
                    new DashboardFrame().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Admin credentials!");
                }
                rs.close();
                
            } else if (role.equals("FACULTY")) {
                int facultyId = Integer.parseInt(username);
                String query = "SELECT faculty_id, name FROM faculty WHERE faculty_id = " + facultyId + 
                              " AND password = '" + password + "' AND status = 'active'";
                ResultSet rs = DBConnection.executeQuery(query);
                
                if (rs.next()) {
                    UserSessionManager.setUser(rs.getInt("faculty_id"), "FACULTY", rs.getString("name"));
                    JOptionPane.showMessageDialog(this, "Welcome Faculty: " + rs.getString("name"));
                    new FacultyDashboardFrame().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Faculty credentials!\nUse Faculty ID as username");
                }
                rs.close();
                
            } else if (role.equals("STUDENT")) {
                int studentId = Integer.parseInt(username);
                String query = "SELECT student_id, name FROM student WHERE student_id = " + studentId + 
                              " AND password = '" + password + "' AND status = 'active'";
                ResultSet rs = DBConnection.executeQuery(query);
                
                if (rs.next()) {
                    UserSessionManager.setUser(rs.getInt("student_id"), "STUDENT", rs.getString("name"));
                    JOptionPane.showMessageDialog(this, "Welcome Student: " + rs.getString("name"));
                    new StudentDashboardFrame().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Student credentials!\nUse Student ID as username");
                }
                rs.close();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "For Faculty/Student, use numeric ID as username!");
        }
    }
    
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        roleCombo.setSelectedIndex(0);
    }
}

// ==============================================
// 16. StudentCourseRegistrationFrame Class
// ==============================================
class StudentCourseRegistrationFrame extends JFrame {
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private int studentId;
    
    public StudentCourseRegistrationFrame() {
        studentId = UserSessionManager.getUserId();
        
        setTitle("Course Registration - " + UserSessionManager.getUserName());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Student: " + UserSessionManager.getUserName()));
        
        JButton registerButton = new JButton("Register for Selected Courses");
        registerButton.addActionListener(e -> registerCourses());
        topPanel.add(registerButton);
        
        String[] columns = {"Select", "Course ID", "Course Name", "Credits", "Department"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        
        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(30);
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        
        loadAvailableCourses();
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadAvailableCourses() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT c.course_id, c.course_name, c.credits, d.department_name " +
                          "FROM course c " +
                          "LEFT JOIN department d ON c.department_id = d.department_id " +
                          "WHERE c.course_id NOT IN (SELECT course_id FROM registration WHERE student_id = " + studentId + ")";
            
            ResultSet rs = DBConnection.executeQuery(query);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    false,
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getInt("credits"),
                    rs.getString("department_name")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void registerCourses() {
        int registeredCount = 0;
        
        for (int i = 0; i < courseTable.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, 0);
            boolean isSelected = false;
            if (value instanceof Boolean) {
                isSelected = (Boolean) value;
            } else if (value instanceof String) {
                isSelected = "true".equalsIgnoreCase((String) value);
            }
            
            if (isSelected) {
                int courseId = (int) tableModel.getValueAt(i, 1);
                
                String query = "INSERT INTO registration (student_id, course_id, grade) VALUES (" + studentId + ", " + courseId + ", 'Pending')";
                int result = DBConnection.executeUpdate(query);
                
                if (result > 0) {
                    registeredCount++;
                }
            }
        }
        
        if (registeredCount > 0) {
            JOptionPane.showMessageDialog(this, "Registered for " + registeredCount + " courses!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No courses selected!");
        }
    }
}

// ==============================================
// 17. StudentDashboardFrame Class
// ==============================================
class StudentDashboardFrame extends JFrame {
    
    public StudentDashboardFrame() {
        setTitle("Student Dashboard - Welcome " + UserSessionManager.getUserName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu regMenu = new JMenu("Registration");
        JMenuItem registerItem = new JMenuItem("Register for Courses");
        registerItem.addActionListener(e -> new StudentCourseRegistrationFrame().setVisible(true));
        regMenu.add(registerItem);
        
        JMenuItem viewRegItem = new JMenuItem("My Registrations & Grades");
        viewRegItem.addActionListener(e -> new StudentGradeViewFrame().setVisible(true));
        regMenu.add(viewRegItem);
        
        JMenu scheduleMenu = new JMenu("Schedule");
        JMenuItem viewScheduleItem = new JMenuItem("View Class Schedule");
        viewScheduleItem.addActionListener(e -> new ViewScheduleFrame().setVisible(true));
        scheduleMenu.add(viewScheduleItem);
        
        JMenu facultyMenu = new JMenu("Faculty");
        JMenuItem viewFacultyItem = new JMenuItem("View Faculty Information");
        viewFacultyItem.addActionListener(e -> new StudentFacultyViewFrame().setVisible(true));
        facultyMenu.add(viewFacultyItem);
        
        JMenu logoutMenu = new JMenu("System");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                           "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserSessionManager.logout();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        logoutMenu.add(logoutItem);
        
        menuBar.add(regMenu);
        menuBar.add(scheduleMenu);
        menuBar.add(facultyMenu);
        menuBar.add(logoutMenu);
        
        setJMenuBar(menuBar);
        
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(240, 248, 255));
        JLabel welcomeLabel = new JLabel("Welcome " + UserSessionManager.getUserName() + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 51, 102));
        welcomePanel.add(welcomeLabel);
        
        JLabel infoLabel = new JLabel("You can register for courses, view your grades, and check schedule", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomePanel.add(infoLabel);
        
        add(welcomePanel);
    }
}

// ==============================================
// 18. StudentGradeViewFrame Class
// ==============================================
class StudentGradeViewFrame extends JFrame {
    
    public StudentGradeViewFrame() {
        int studentId = UserSessionManager.getUserId();
        String studentName = UserSessionManager.getUserName();
        
        setTitle("My Grades - " + studentName);
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        String[] columns = {"Course Name", "Credits", "Grade", "Registration Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable gradeTable = new JTable(tableModel);
        
        try {
            String query = "SELECT c.course_name, c.credits, r.grade, r.registration_date " +
                          "FROM registration r " +
                          "JOIN course c ON r.course_id = c.course_id " +
                          "WHERE r.student_id = " + studentId +
                          " ORDER BY r.registration_date DESC";
            
            ResultSet rs = DBConnection.executeQuery(query);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("course_name"),
                    rs.getInt("credits"),
                    rs.getString("grade"),
                    rs.getTimestamp("registration_date")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JLabel summaryLabel = new JLabel("Student: " + studentName + " (ID: " + studentId + ")", JLabel.CENTER);
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(summaryLabel, BorderLayout.NORTH);
    }
}

// ==============================================
// 19. StudentManagementFrame Class
// ==============================================
class StudentManagementFrame extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField, phoneField;
    private JComboBox<String> deptCombo;
    private int selectedStudentId = -1;
    
    public StudentManagementFrame() {
        setTitle("Manage Students");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        formPanel.setPreferredSize(new Dimension(300, 200));
        
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);
        
        formPanel.add(new JLabel("Department:"));
        deptCombo = new JComboBox<>();
        loadDepartments();
        formPanel.add(deptCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        String[] columns = {"ID", "Name", "Email", "Phone", "Department"};
        tableModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> loadSelectedStudent());
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadStudents();
    }
    
    private void loadDepartments() {
        try {
            String query = "SELECT department_id, department_name FROM department";
            ResultSet rs = DBConnection.executeQuery(query);
            deptCombo.removeAllItems();
            while (rs.next()) {
                deptCombo.addItem(rs.getInt("department_id") + " - " + rs.getString("department_name"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadStudents() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT s.*, d.department_name FROM student s " +
                          "LEFT JOIN department d ON s.department_id = d.department_id";
            ResultSet rs = DBConnection.executeQuery(query);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("department_name")
                };
                tableModel.addRow(row);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addStudent() {
        String studentIdStr = JOptionPane.showInputDialog(this, "Enter Student ID (e.g., 1001):");
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String deptSelected = (String) deptCombo.getSelectedItem();
        int deptId = Integer.parseInt(deptSelected.split(" - ")[0]);
        
        if (studentIdStr == null || studentIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required!");
            return;
        }
        
        int studentId = Integer.parseInt(studentIdStr);
        
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required!");
            return;
        }
        
        String checkQuery = "SELECT student_id FROM student WHERE student_id = " + studentId;
        ResultSet rs = DBConnection.executeQuery(checkQuery);
        try {
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Student ID already exists! Use a different ID.");
                rs.close();
                return;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String query = String.format("INSERT INTO student (student_id, name, email, phone, department_id, password, status) VALUES (%d, '%s', '%s', '%s', %d, 'student123', 'active')",
                  studentId, name, email, phone, deptId);
        
        int result = DBConnection.executeUpdate(query);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Student added successfully! ID: " + studentId);
            loadStudents();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add student!");
        }
    }
    
    private void updateStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update!");
            return;
        }
        
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String deptSelected = (String) deptCombo.getSelectedItem();
        int deptId = Integer.parseInt(deptSelected.split(" - ")[0]);
        
        String query = String.format("UPDATE student SET name='%s', email='%s', phone='%s', department_id=%d WHERE student_id=%d",
                      name, email, phone, deptId, selectedStudentId);
        
        int result = DBConnection.executeUpdate(query);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Student updated successfully!");
            loadStudents();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update student!");
        }
    }
    
    private void deleteStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", 
                       "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM student WHERE student_id=" + selectedStudentId;
            int result = DBConnection.executeUpdate(query);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                loadStudents();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student!");
            }
        }
    }
    
    private void loadSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedStudentId = (int) tableModel.getValueAt(selectedRow, 0);
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            emailField.setText((String) tableModel.getValueAt(selectedRow, 2));
            phoneField.setText((String) tableModel.getValueAt(selectedRow, 3));
            
            String deptName = (String) tableModel.getValueAt(selectedRow, 4);
            for (int i = 0; i < deptCombo.getItemCount(); i++) {
                if (deptCombo.getItemAt(i).contains(deptName)) {
                    deptCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void clearForm() {
        selectedStudentId = -1;
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        deptCombo.setSelectedIndex(0);
        studentTable.clearSelection();
    }
}

// ==============================================
// 20. UpdateGradeFrame Class
// ==============================================
class UpdateGradeFrame extends JFrame {
    private JComboBox<String> courseCombo;
    private JComboBox<String> studentCombo;
    private JComboBox<String> gradeCombo;
    
    public UpdateGradeFrame() {
        setTitle("Update Student Grades - " + UserSessionManager.getUserName());
        setSize(550, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));
        
        add(new JLabel("Select Course:"));
        courseCombo = new JComboBox<>();
        loadCourses();
        courseCombo.addActionListener(e -> loadStudentsForCourse());
        add(courseCombo);
        
        add(new JLabel("Select Student:"));
        studentCombo = new JComboBox<>();
        add(studentCombo);
        
        add(new JLabel("Select Grade:"));
        gradeCombo = new JComboBox<>(new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F", "Pending"});
        add(gradeCombo);
        
        JButton updateButton = new JButton("Update Grade");
        updateButton.addActionListener(e -> updateGrade());
        add(updateButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            loadCourses();
            loadStudentsForCourse();
        });
        add(refreshButton);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton);
        
        JLabel roleLabel = new JLabel("Role: " + UserSessionManager.getUserType() + 
            " - " + (UserSessionManager.isAdmin() ? "Can update any course" : "Can only update assigned courses"));
        roleLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        roleLabel.setForeground(Color.BLUE);
        add(roleLabel);
        add(new JLabel(""));
    }
    
    private void loadCourses() {
        courseCombo.removeAllItems();
        try {
            String query;
            
            if (UserSessionManager.isAdmin()) {
                query = "SELECT course_id, course_name FROM course ORDER BY course_id";
            } else {
                int facultyId = UserSessionManager.getUserId();
                query = "SELECT DISTINCT c.course_id, c.course_name " +
                       "FROM course c " +
                       "JOIN class cl ON c.course_id = cl.course_id " +
                       "WHERE cl.faculty_id = " + facultyId +
                       " ORDER BY c.course_id";
            }
            
            ResultSet rs = DBConnection.executeQuery(query);
            boolean hasCourses = false;
            while (rs.next()) {
                hasCourses = true;
                courseCombo.addItem(rs.getInt("course_id") + " - " + rs.getString("course_name"));
            }
            rs.close();
            
            if (!hasCourses) {
                courseCombo.addItem("No courses available");
                courseCombo.setEnabled(false);
            } else {
                courseCombo.setEnabled(true);
                if (courseCombo.getItemCount() > 0) {
                    loadStudentsForCourse();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
        }
    }
    
    private void loadStudentsForCourse() {
        studentCombo.removeAllItems();
        String selected = (String) courseCombo.getSelectedItem();
        if (selected == null || selected.equals("No courses available")) return;
        
        int courseId = Integer.parseInt(selected.split(" - ")[0]);
        
        try {
            String query;
            
            if (UserSessionManager.isAdmin()) {
                query = "SELECT s.student_id, s.name, " +
                       "COALESCE(r.grade, 'Not Registered') as current_grade " +
                       "FROM student s " +
                       "LEFT JOIN registration r ON s.student_id = r.student_id AND r.course_id = " + courseId + " " +
                       "ORDER BY s.student_id";
            } else {
                query = "SELECT s.student_id, s.name, " +
                       "COALESCE(r.grade, 'No Grade') as current_grade " +
                       "FROM registration r " +
                       "JOIN student s ON r.student_id = s.student_id " +
                       "WHERE r.course_id = " + courseId +
                       " ORDER BY s.student_id";
            }
            
            ResultSet rs = DBConnection.executeQuery(query);
            boolean hasStudents = false;
            while (rs.next()) {
                hasStudents = true;
                String currentGrade = rs.getString("current_grade");
                studentCombo.addItem(rs.getInt("student_id") + " - " + rs.getString("name") + 
                                    " [Current: " + currentGrade + "]");
            }
            rs.close();
            
            if (!hasStudents) {
                if (UserSessionManager.isAdmin()) {
                    studentCombo.addItem("No students found in system");
                } else {
                    studentCombo.addItem("No students registered for this course");
                }
                studentCombo.setEnabled(false);
            } else {
                studentCombo.setEnabled(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }
    
    private void updateGrade() {
        String courseSelected = (String) courseCombo.getSelectedItem();
        String studentSelected = (String) studentCombo.getSelectedItem();
        String newGrade = (String) gradeCombo.getSelectedItem();
        
        if (courseSelected == null || studentSelected == null || 
            courseSelected.equals("No courses available") ||
            studentSelected.equals("No students found in system") || 
            studentSelected.equals("No students registered for this course")) {
            JOptionPane.showMessageDialog(this, "Please select valid course and student!");
            return;
        }
        
        int courseId = Integer.parseInt(courseSelected.split(" - ")[0]);
        int studentId = Integer.parseInt(studentSelected.split(" - ")[0]);
        
        try {
            String checkQuery = "SELECT COUNT(*) FROM registration WHERE student_id = " + studentId + 
                               " AND course_id = " + courseId;
            ResultSet rs = DBConnection.executeQuery(checkQuery);
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            
            if (count == 0) {
                if (UserSessionManager.isAdmin()) {
                    int response = JOptionPane.showConfirmDialog(this, 
                        "Student is not registered for this course.\nDo you want to register them and assign this grade?",
                        "Register Student", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        String insertQuery = "INSERT INTO registration (student_id, course_id, grade) VALUES (" +
                                            studentId + ", " + courseId + ", '" + newGrade + "')";
                        int result = DBConnection.executeUpdate(insertQuery);
                        if (result > 0) {
                            JOptionPane.showMessageDialog(this, "Student registered and grade set to " + newGrade + "!");
                            loadStudentsForCourse();
                        } else {
                            JOptionPane.showMessageDialog(this, "Registration failed!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Student is not registered for this course!");
                }
                return;
            }
            
            String updateQuery = "UPDATE registration SET grade = '" + newGrade + 
                                "' WHERE student_id = " + studentId + " AND course_id = " + courseId;
            
            int result = DBConnection.executeUpdate(updateQuery);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Grade updated successfully to " + newGrade + "!");
                loadStudentsForCourse();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating grade: " + e.getMessage());
        }
    }
}

// ==============================================
// 21. StudentFacultyViewFrame Class
// ==============================================
class StudentFacultyViewFrame extends JFrame {
    private JTable facultyTable;
    private DefaultTableModel tableModel;
    
    public StudentFacultyViewFrame() {
        setTitle("Faculty Information");
        setSize(700, 400);
        setLocationRelativeTo(null);
        
        String[] columns = {"Faculty ID", "Name", "Email", "Department"};
        tableModel = new DefaultTableModel(columns, 0);
        facultyTable = new JTable(tableModel);
        
        loadFaculty();
        
        JScrollPane scrollPane = new JScrollPane(facultyTable);
        add(scrollPane);
    }
    
    private void loadFaculty() {
        try {
            String query = "SELECT f.faculty_id, f.name, f.email, d.department_name " +
                          "FROM faculty f " +
                          "LEFT JOIN department d ON f.department_id = d.department_id " +
                          "WHERE f.status = 'active'";
            
            ResultSet rs = DBConnection.executeQuery(query);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("faculty_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department_name")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// ==============================================
// 22. ViewRegistrationsFrame Class
// ==============================================
class ViewRegistrationsFrame extends JFrame {
    private JTable regTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> studentCombo;
    private boolean isAdmin;
    
    public ViewRegistrationsFrame() {
        isAdmin = UserSessionManager.isAdmin();
        
        setTitle(isAdmin ? "All Student Registrations - Admin" : "My Registrations - " + UserSessionManager.getUserName());
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Student:"));
        studentCombo = new JComboBox<>();
        loadStudents();
        studentCombo.addActionListener(e -> loadRegistrations());
        topPanel.add(studentCombo);
        
        if (!isAdmin) {
            studentCombo.setEnabled(false);
            int userId = UserSessionManager.getUserId();
            for (int i = 0; i < studentCombo.getItemCount(); i++) {
                String item = studentCombo.getItemAt(i);
                if (item != null && item.startsWith(String.valueOf(userId))) {
                    studentCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        String[] columns = {"Reg ID", "Student ID", "Student Name", "Course ID", "Course Name", "Grade", "Registration Date"};
        tableModel = new DefaultTableModel(columns, 0);
        regTable = new JTable(tableModel);
        regTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(regTable);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadStudents() {
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT student_id, name FROM student WHERE status = 'active' ORDER BY student_id");
            studentCombo.addItem("-- Select Student --");
            while (rs.next()) {
                studentCombo.addItem(rs.getInt("student_id") + " - " + rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadRegistrations() {
        tableModel.setRowCount(0);
        String selected = (String) studentCombo.getSelectedItem();
        
        if (selected == null || selected.equals("-- Select Student --")) {
            if (!isAdmin) {
                return;
            }
            return;
        }
        
        int studentId = Integer.parseInt(selected.split(" - ")[0]);
        
        try {
            String query = "SELECT r.registration_id, r.student_id, s.name as student_name, " +
                          "r.course_id, c.course_name, r.grade, r.registration_date " +
                          "FROM registration r " +
                          "JOIN student s ON r.student_id = s.student_id " +
                          "JOIN course c ON r.course_id = c.course_id " +
                          "WHERE r.student_id = " + studentId +
                          " ORDER BY r.registration_date DESC";
            
            ResultSet rs = DBConnection.executeQuery(query);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("registration_id"),
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("grade"),
                    rs.getTimestamp("registration_date")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// ==============================================
// 23. ViewScheduleFrame Class
// ==============================================
class ViewScheduleFrame extends JFrame {
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    
    public ViewScheduleFrame() {
        setTitle("Class Schedule");
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        String[] columns = {"Course Name", "Faculty", "Room No", "Day", "Time"};
        tableModel = new DefaultTableModel(columns, 0);
        scheduleTable = new JTable(tableModel);
        
        loadSchedule();
        
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        add(scrollPane);
    }
    
    private void loadSchedule() {
        try {
            String query = "SELECT c.course_name, f.name as faculty_name, cl.room_no, cs.day, cs.time " +
                          "FROM class_schedule cs " +
                          "JOIN class cl ON cs.class_id = cl.class_id " +
                          "JOIN course c ON cl.course_id = c.course_id " +
                          "LEFT JOIN faculty f ON cl.faculty_id = f.faculty_id " +
                          "ORDER BY FIELD(cs.day, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'), cs.time";
            
            ResultSet rs = DBConnection.executeQuery(query);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("course_name"),
                    rs.getString("faculty_name"),
                    rs.getString("room_no"),
                    rs.getString("day"),
                    rs.getString("time")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// ==============================================
// MAIN CLASS - UniversityManagementSystem1
// ==============================================
public class UniversityManagementSystem1 {
    public static void main(String[] args) {
        // Initialize database connection
        DBConnection.getConnection();
        
        // Launch the login frame
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}