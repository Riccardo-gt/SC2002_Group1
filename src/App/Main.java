package App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import Models.Entities.*;
import Models.Utility_Classes.*;

/**
 * Main application class for the Internship Placement Management System.
 * Serves as the entry point and orchestrates the entire application flow.
 *
 * <p>This class is responsible for:</p>
 * <ul>
 *   <li>Initializing the system by loading data from CSV files</li>
 *   <li>Managing the main application loop</li>
 *   <li>Routing users to appropriate menus based on their role</li>
 *   <li>Coordinating data persistence operations</li>
 *   <li>Managing user sessions (login/logout)</li>
 * </ul>
 *
 * <p>The application supports three user types:</p>
 * <ul>
 *   <li><b>Students:</b> Browse and apply for internships</li>
 *   <li><b>Company Representatives:</b> Create and manage internship opportunities</li>
 *   <li><b>Career Centre Staff:</b> Administrative oversight and approvals</li>
 * </ul>
 *
 * <p>Data persistence is handled through CSV files in the Datasets directory.</p>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
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

    /**
     * Main entry point for the Internship Placement Management System.
     * Initializes the system, loads data, and starts the main application loop.
     *
     * @param args command-line arguments (not used)
     */
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

    /**
     * Loads all system data from CSV files.
     * This method orchestrates the loading of students, staff, company representatives,
     * internships, and applications in the correct order to maintain referential integrity.
     */
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

    /**
     * Loads student accounts from the student_list.csv file.
     * Each student is registered with their user ID, name, major, year, email, password, and CGPA.
     */
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

    /**
     * Loads career centre staff accounts from the staff_list.csv file.
     * Each staff member is registered with their user ID, name, role, department, and email.
     */
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

    /**
     * Loads company representatives from the company_reps.csv file.
     * Approved representatives are added to registered accounts, while pending ones
     * are added to the pending list for staff review.
     */
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

    /**
     * Loads internships from the internships.csv file.
     * Links each internship to its company representative based on the stored representative ID.
     */
    static void loadInternships() {
        allInternships = FileIOHandler.loadInternships(registeredAccounts);
        System.out.println("Internships loaded");
    }

    /**
     * Loads applications from the applications.csv file.
     * Links each application to its corresponding student and internship.
     */
    static void loadApplications() {
        FileIOHandler.loadApplications(allInternships, registeredAccounts);
        System.out.println("Applications loaded");
    }

    /**
     * Saves all system data to CSV files.
     * This includes internships, applications, and company representatives (both approved and pending).
     */
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

    /**
     * Displays the main login menu and handles user choices.
     * Provides options for login, company representative registration, and exit.
     */
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

    /**
     * Routes the currently logged-in user to their appropriate menu.
     * Determines the user type and displays the corresponding menu interface.
     */
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

    /**
     * Displays the student menu and handles student-specific operations.
     * Provides options for viewing internships, applying, managing applications,
     * accepting placements, requesting withdrawals, and changing password.
     */
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

    /**
     * Displays the company representative menu and handles company rep operations.
     * Provides options for creating internships, managing internship listings,
     * viewing applications, and managing application statuses.
     */
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

    /**
     * Displays the career centre staff menu and handles administrative operations.
     * Provides options for authorizing company representatives, approving internships,
     * managing withdrawal requests, generating reports, and viewing all internships.
     */
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

    /**
     * Logs out the current user and saves all data.
     * Resets the currentUser to null, returning to the login menu.
     */
    static void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
        saveAllData();
    }
}