package com.example.studentmanagementsystem;

import java.io.*;
import java.util.*;

// ============================================================
// Student class: ek student ka data (roll, naam, marks) rakhti hai
// ============================================================
class Student {
    // private = bahar se seedha access nahi (encapsulation)
    private int rollNo;
    private String name;
    private int marks;

    // Constructor: object banate waqt values set karta hai
    public Student(int rollNo, String name, int marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.marks = marks;
    }

    // Getters: private values ko padhne ke liye
    public int getRollNo() { return rollNo; }
    public String getName() { return name; }
    public int getMarks() { return marks; }

    // Marks ke hisaab se grade return karta hai
    public String getGrade() {
        if (marks >= 90) return "A+";
        else if (marks >= 80) return "A";
        else if (marks >= 70) return "B";
        else if (marks >= 60) return "C";
        else if (marks >= 40) return "D";
        else return "F";
    }

    // Screen par dikhane ke liye format
    @Override
    public String toString() {
        return String.format("Roll: %-6d Name: %-20s Marks: %-4d Grade: %s",
                rollNo, name, marks, getGrade());
    }

    // File me save karne ke liye format (comma separated)
    public String toFileString() {
        return rollNo + "," + name + "," + marks;
    }
}

// ============================================================
// Main class: menu aur saare features yahan hain
// ============================================================
public class Main {

    // ArrayList: students ki list (size apne aap badhti hai)
    static ArrayList<Student> students = new ArrayList<>();

    // Scanner: keyboard se input lene ke liye
    static Scanner sc = new Scanner(System.in);

    // Is file me data save hoga
    static final String FILE_NAME = getFilePath();

// Ek aisa folder dhundta hai jahan file likhi ja sake
static String getFilePath() {
    String[] folders = {
        System.getProperty("user.dir"),
        System.getProperty("user.home"),
        System.getProperty("java.io.tmpdir")
    };
    for (String folder : folders) {
        if (folder == null) continue;
        File dir = new File(folder);
        if (dir.exists() && dir.canWrite()) {
            return new File(dir, "students.txt").getAbsolutePath();
        }
    }
    return "students.txt";
}

    public static void main(String[] args) {
        loadFromFile(); // program start hote hi purana data load karo
        System.out.println("Data file: " + FILE_NAME);

        while (true) { // jab tak user exit na kare, menu dikhta rahega
            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student by Roll No");
            System.out.println("4. Update Marks");
            System.out.println("5. Delete Student");
            System.out.println("6. Show Topper");
            System.out.println("7. Exit");
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: addStudent(); break;
                case 2: viewAll(); break;
                case 3: searchStudent(); break;
                case 4: updateMarks(); break;
                case 5: deleteStudent(); break;
                case 6: showTopper(); break;
                case 7:
                    saveToFile();
                    System.out.println("Data saved. Goodbye!");
                    return; // program band
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ---------- Helper: safe number input ----------
    // Agar user number ki jagah text daale to program crash nahi hoga
    static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // ---------- Helper: roll number se student dhoondo ----------
    static Student findByRoll(int roll) {
        for (Student s : students) {
            if (s.getRollNo() == roll) return s;
        }
        return null; // nahi mila
    }

    // ---------- 1. Add ----------
    static void addStudent() {
        int roll = readInt("Enter Roll No: ");
        if (findByRoll(roll) != null) {
            System.out.println("This roll number already exists!");
            return;
        }
        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim().replace(",", " "); // comma file format tod dega

        int marks = readInt("Enter Marks (0-100): ");
        if (marks < 0 || marks > 100) {
            System.out.println("Marks must be between 0 and 100.");
            return;
        }

        students.add(new Student(roll, name, marks));
        saveToFile();
        System.out.println("Student added successfully!");
    }

    // ---------- 2. View all ----------
    static void viewAll() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("\n--- All Students ---");
        for (Student s : students) {
            System.out.println(s);
        }
        System.out.println("Total students: " + students.size());
    }

    // ---------- 3. Search ----------
    static void searchStudent() {
        int roll = readInt("Enter Roll No to search: ");
        Student s = findByRoll(roll);
        if (s == null) System.out.println("Student not found.");
        else System.out.println(s);
    }

    // ---------- 4. Update ----------
    static void updateMarks() {
        int roll = readInt("Enter Roll No to update: ");
        Student s = findByRoll(roll);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        int newMarks = readInt("Enter new marks (0-100): ");
        if (newMarks < 0 || newMarks > 100) {
            System.out.println("Marks must be between 0 and 100.");
            return;
        }
        // Student class me setter nahi hai, isliye purana hata kar naya add karte hain
        students.remove(s);
        students.add(new Student(s.getRollNo(), s.getName(), newMarks));
        saveToFile();
        System.out.println("Marks updated!");
    }

    // ---------- 5. Delete ----------
    static void deleteStudent() {
        int roll = readInt("Enter Roll No to delete: ");
        Student s = findByRoll(roll);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        students.remove(s);
        saveToFile();
        System.out.println("Student deleted.");
    }

    // ---------- 6. Topper ----------
    static void showTopper() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        Student topper = students.get(0);
        for (Student s : students) {
            if (s.getMarks() > topper.getMarks()) topper = s;
        }
        System.out.println("Topper: " + topper);
    }

    // ---------- File: save ----------
    // BufferedWriter + FileWriter se text file me likhte hain
    static void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                bw.write(s.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // ---------- File: load ----------
    // BufferedReader se line by line padhte hain
    static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; // pehli baar file nahi hogi, koi baat nahi

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // "1,Sachin,85" -> ["1","Sachin","85"]
                if (parts.length == 3) {
                    int roll = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int marks = Integer.parseInt(parts[2]);
                    students.add(new Student(roll, name, marks));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
}

