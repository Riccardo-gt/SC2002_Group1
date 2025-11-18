package App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import Models.Entities.*;
import Models.Utility_Classes.*;

public class Main {
    static HashMap<String, User> registeredAccounts = new HashMap<>();
    static List<Internship> allInternships = new ArrayList<>();
    static List<CompanyRepresentative> pendingCompanyReps = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static User currentUser = null;

    // Menu instances
    static MainMenu mainMenu;
    static StudentMenu studentMenu;
    static CompanyRepMenu companyRepMenu;
    static StaffMenu staffMenu;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Internship Placement Management System");
        System.out.println("=================================================\n");

        // Load all data
        loadData();

        // Initialize menu instances
        mainMenu = new MainMenu(scanner, registeredAccounts, pendingCompanyReps);
        studentMenu = new StudentMenu(scanner, allInternships);
        companyRepMenu = new CompanyRepMenu(scanner, allInternships);
        staffMenu = new StaffMenu(scanner, allInternships, pendingCompanyReps, registeredAccounts);

        // Main application loop
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                routeToUserMenu();
            }
        }
    }

    // ==================== DATA LOADING ====================

    static void loadData() {
        System.out.println("Loading system data...");
        FileIOHandler.StudentCSVIncludePasswords();
        registerStudents();
        registerStaff();
        loadCompanyReps();
        loadInternships();
        loadApplications();
        System.out.println("System ready!\n");
    }

    static void registerStudents() {
        try {
            BufferedReader file = new BufferedReader(new FileReader("Datasets/student_list.csv"));
            String line = file.readLine(); // Read the header

            while ((line = file.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                // Format: UserID,Name,Major,Year,Email,Password, CGPA
                Student student = new Student(parts[0], parts[1], parts[4], parts[5], parts[2], 
                    Integer.valueOf(parts[3]), Float.valueOf(parts[6]));
                registeredAccounts.put(parts[0], student);
            }
            file.close();
            System.out.println("Students loaded");
        } catch (IOException error) {
            System.out.println("Note: sample_student_list.csv not found. Starting with no students.");
        }
    }

    static void registerStaff() {
        try {
            BufferedReader file = new BufferedReader(new FileReader("Datasets/staff_list.csv"));
            String line = file.readLine();

            while ((line = file.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                // Format: UserID,Name,Role,Department,Email
                CareerCentreStaff staff = new CareerCentreStaff(parts[0], parts[1], parts[4], "password", parts[2],
                        parts[3]);
                registeredAccounts.put(parts[0], staff);
            }
            file.close();
            System.out.println("Staff loaded");
        } catch (IOException error) {
            System.out.println("Note: sample_staff_list.csv not found. Starting with no staff.");
        }
    }

    static void loadCompanyReps() {
        List<CompanyRepresentative> reps = FileIOHandler.loadCompanyReps();
        for (CompanyRepresentative rep : reps) {
            if ("APPROVED".equals(rep.getStatus())) {
                registeredAccounts.put(rep.getUserID(), rep);
            } else {
                pendingCompanyReps.add(rep);
            }
        }
        System.out.println("Company representatives loaded");
    }

    static void loadInternships() {
        allInternships = FileIOHandler.loadInternships(registeredAccounts);
        System.out.println("Internships loaded");
    }

    static void loadApplications() {
        FileIOHandler.loadApplications(allInternships, registeredAccounts);
        System.out.println("Applications loaded");
    }

    static void saveAllData() {
        List<CompanyRepresentative> allReps = new ArrayList<>();
        for (User user : registeredAccounts.values()) {
            if (user instanceof CompanyRepresentative) {
                allReps.add((CompanyRepresentative) user);
            }
        }
        allReps.addAll(pendingCompanyReps);

        FileIOHandler.saveAllData(allInternships, allReps, registeredAccounts);
        System.out.println("Data saved successfully.");
    }

    // ==================== LOGIN MENU ====================

    static void showLoginMenu() {
        int choice = mainMenu.displayMenu();

        switch (choice) {
            case 1:
                currentUser = mainMenu.login();
                break;
            case 2:
                mainMenu.registerCompanyRep();
                saveAllData();
                break;
            case 3:
                saveAllData();
                System.out.println("\nThank you for using the system. Goodbye!");
                System.exit(0);
                break;
            default:
                if (choice != -1) {
                    System.out.println("Invalid option. Please try again.");
                }
        }
    }

    // ==================== ROUTING ====================

    static void routeToUserMenu() {
        if (currentUser instanceof Student) {
            showStudentMenu();
        } else if (currentUser instanceof CompanyRepresentative) {
            showCompanyRepMenu();
        } else if (currentUser instanceof CareerCentreStaff) {
            showStaffMenu();
        }
    }

    // ==================== STUDENT MENU ====================

    static void showStudentMenu() {
        Student student = (Student) currentUser;
        int choice = studentMenu.displayMenu();

        switch (choice) {
            case 1:
                studentMenu.viewAvailableInternships(student);
                break;
            case 2:
                studentMenu.applyForInternship(student);
                saveAllData();
                break;
            case 3:
                student.viewAppliedInternships();
                break;
            case 4:
                studentMenu.acceptPlacement(student);
                saveAllData();
                break;
            case 5:
                studentMenu.requestWithdrawal(student);
                break;
            case 6:
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case 7:
                logout();
                break;
            default:
                if (choice != -1) {
                    System.out.println("Invalid option.");
                }
        }
    }

    // ==================== COMPANY REP MENU ====================

    static void showCompanyRepMenu() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        int choice = companyRepMenu.displayMenu();

        switch (choice) {
            case 1:
                companyRepMenu.createInternship(rep);
                saveAllData();
                break;
            case 2:
                companyRepMenu.viewMyInternships(rep);
                break;
            case 3:
                companyRepMenu.editInternship(rep);
                saveAllData();
                break;
            case 4:
                companyRepMenu.deleteInternship(rep);
                saveAllData();
                break;
            case 5:
                rep.viewApplications();
                break;
            case 6:
                companyRepMenu.manageApplications(rep);
                saveAllData();
                break;
            case 7:
                companyRepMenu.toggleVisibility(rep);
                saveAllData();
                break;
            case 8:
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case 9:
                logout();
                break;
            default:
                if (choice != -1) {
                    System.out.println("Invalid option.");
                }
        }
    }

    // ==================== STAFF MENU ====================

    static void showStaffMenu() {
        CareerCentreStaff staff = (CareerCentreStaff) currentUser;
        int choice = staffMenu.displayMenu();

        switch (choice) {
            case 1:
                staffMenu.authorizeCompanyReps(staff);
                saveAllData();
                break;
            case 2:
                staffMenu.approveInternships(staff);
                saveAllData();
                break;
            case 3:
                staffMenu.manageWithdrawalRequests(staff);
                saveAllData();
                break;
            case 4:
                staffMenu.generateReports(staff);
                break;
            case 5:
                staffMenu.viewAllInternships();
                break;
            case 6:
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case 7:
                logout();
                break;
            default:
                if (choice != -1) {
                    System.out.println("Invalid option.");
                }
        }
    }

    // ==================== COMMON ====================

    static void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
        saveAllData();
    }
}