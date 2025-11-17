package App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import Models.Entities.*;
import Models.Utility_Classes.*;

public class Main {
    static HashMap<String, User> registeredAccounts = new HashMap<>();
    static List<Internship> allInternships = new ArrayList<>();
    static List<CompanyRepresentative> pendingCompanyReps = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Internship Placement Management System");
        System.out.println("=================================================\n");

        // Load all data
        loadData();
        System.out.println("registered accounts = " + registeredAccounts);
        // App.Main application loop
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
        FileIOHandler.StaffCSVIncludePasswords();
        FileIOHandler.CompanyRepCSVIncludePasswords();
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
                // Format: UserID,Name,Major,Year,Email,Password
                Student student = new Student(parts[0], parts[1], parts[4], parts[5], parts[2],
                        Integer.valueOf(parts[3]));
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
                // Format: UserID,Name,Role,Department,Email,Password
                CareerCentreStaff staff = new CareerCentreStaff(parts[0], parts[1], parts[4], parts[5], parts[2],
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
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           MAIN MENU                    ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  1. Login                              ║");
        System.out.println("║  2. Register as Company Representative ║");
        System.out.println("║  3. Exit                               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Select option: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                registerCompanyRep();
                break;
            case 3:
                saveAllData();
                System.out.println("\nThank you for using the system. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    static void login() {
        System.out.print("\nEnter User ID: ");
        String userId = scanner.nextLine().trim();
        User user = registeredAccounts.get(userId);
        if (user == null) {
            System.out.println("\nInvalid username. Please try again.");
            return;
        }
        System.out.print("Enter Password or click 1 if you forgot your password and wish to reset it: ");
        String password = scanner.nextLine().trim();
        if (password.equals("1")) {
            user.resetPassword(scanner);
        }
        else if (!user.login(password)) {
            System.out.println("\nInvalid password. Please try again.");
            return;
        }
        currentUser = user;
        System.out.println("\nLogin successful! Welcome, " + currentUser.getName());
    }

    static void registerCompanyRep() {
        System.out.println("\n=== Company Representative Registration ===");
        System.out.print("Email (will be your User ID): ");
        String email = scanner.nextLine().trim();

        if (registeredAccounts.containsKey(email)) {
            System.out.println("Error: This email is already registered.");
            return;
        }

        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Company Name: ");
        String company = scanner.nextLine().trim();
        System.out.print("Department: ");
        String department = scanner.nextLine().trim();
        System.out.print("Position: ");
        String position = scanner.nextLine().trim();

        CompanyRepresentative rep = new CompanyRepresentative(
                email, name, email, password, company, position, department, "PENDING");

        pendingCompanyReps.add(rep);
        saveAllData();

        System.out.println("\nRegistration submitted! Please wait for Career Centre Staff approval.");
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

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         STUDENT MENU                     ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. View Available Internships           ║");
        System.out.println("║  2. Apply for Internship                 ║");
        System.out.println("║  3. View My Applications                 ║");
        System.out.println("║  4. Accept Placement                     ║");
        System.out.println("║  5. Request Withdrawal                   ║");
        System.out.println("║  6. Change Password                      ║");
        System.out.println("║  7. Logout                               ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Select option: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        switch (choice) {
            case 1:
                viewAvailableInternshipsForStudent(student);
                break;
            case 2:
                applyForInternship(student);
                break;
            case 3:
                student.viewAppliedInternships();
                break;
            case 4:
                acceptPlacement(student);
                break;
            case 5:
                requestWithdrawal(student);
                break;
            case 6:
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case 7:
                logout();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    static void viewAvailableInternshipsForStudent(Student student) {
        List<Internship> available = allInternships.stream()
                .filter(i -> i.isAcceptingApplications())
                .filter(i -> student.isEligibleForLevel(i.getLevel()))
                .filter(i -> student.matchesMajor(i.getPreferredMajor()))
                .sorted((i1, i2) -> i1.getTitle().compareToIgnoreCase(i2.getTitle()))
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println("\nNo internships available for your profile.");
            return;
        }

        System.out.println("\n=== Available Internships ===");
        for (int i = 0; i < available.size(); i++) {
            Internship internship = available.get(i);
            System.out.println("\n[" + (i + 1) + "] " + internship.getTitle());
            System.out.println("    Company: " + (internship.getCompanyRepresentative() != null
                    ? internship.getCompanyRepresentative().getCompanyName()
                    : "N/A"));
            System.out.println("    Level: " + internship.getLevel());
            System.out.println("    Preferred Major: " + internship.getPreferredMajor());
            System.out.println("    Description: " + internship.getDescription());
            System.out.println("    Period: " + internship.getOpeningDate() + " to " + internship.getClosingDate());
            System.out.println("    Slots: " + internship.getSlots());
        }
    }

    static void applyForInternship(Student student) {
        List<Internship> available = allInternships.stream()
                .filter(i -> i.isAcceptingApplications())
                .filter(i -> student.isEligibleForLevel(i.getLevel()))
                .filter(i -> student.matchesMajor(i.getPreferredMajor()))
                .filter(i -> i.getApplications().stream()
                        .noneMatch(app -> app.getStudent().equals(student))) // not already applied
                .sorted((i1, i2) -> i1.getTitle().compareToIgnoreCase(i2.getTitle()))
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println("\nNo internships available.");
            return;
        }

        System.out.println("\n=== Available Internships ===");
        for (int i = 0; i < available.size(); i++) {
            Internship internship = available.get(i);
            System.out.println("\n[" + (i + 1) + "] " + internship.getTitle());
            System.out.println("    Company: " + (internship.getCompanyRepresentative() != null
                    ? internship.getCompanyRepresentative().getCompanyName()
                    : "N/A"));
            System.out.println("    Level: " + internship.getLevel());
            System.out.println("    Preferred Major: " + internship.getPreferredMajor());
            System.out.println("    Description: " + internship.getDescription());
            System.out.println("    Period: " + internship.getOpeningDate() + " to " + internship.getClosingDate());
            System.out.println("    Slots: " + internship.getSlots());
        }

        System.out.print("\nEnter internship number to apply (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice < 1 || choice > available.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Internship internship = available.get(choice - 1);
            if (student.applyForInternship(internship)) {
                saveAllData();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void acceptPlacement(Student student) {
        // Check if already accepted a placement
        boolean hasConfirmedPlacement = student.getApplications().stream()
                .anyMatch(app -> app.isConfirmed());
        if (hasConfirmedPlacement) {
            System.out.println("\nYou have already accepted a placement. No further placements can be accepted.");
            return;
        }

        List<Application> approved = student.getApplications().stream()
                .filter(app -> app.getStatus() == Application.ApplicationStatus.SUCCESSFUL)
                .filter(app -> !app.isConfirmed())
                .collect(Collectors.toList());

        if (approved.isEmpty()) {
            System.out.println("\nNo approved applications to accept.");
            return;
        }

        System.out.println("\n=== Approved Applications ===");
        for (int i = 0; i < approved.size(); i++) {
            Application app = approved.get(i);
            System.out.println((i + 1) + ". " + app.getInternship().getTitle());
        }

        System.out.print("\nEnter number to accept (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice < 1 || choice > approved.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            if (student.acceptPlacement(approved.get(choice - 1))) {
                saveAllData();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void requestWithdrawal(Student student) {
        if (student.getApplications().isEmpty()) {
            System.out.println("\nNo applications to withdraw.");
            return;
        }

        student.viewAppliedInternships();
        System.out.print("\nEnter application number to withdraw (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice < 1 || choice > student.getApplications().size()) {
                System.out.println("Invalid selection.");
                return;
            }

            student.requestWithdrawal(student.getApplications().get(choice - 1));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    // ==================== COMPANY REP MENU ====================

    static void showCompanyRepMenu() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║    COMPANY REPRESENTATIVE MENU           ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Create Internship Opportunity        ║");
        System.out.println("║  2. View My Internships                  ║");
        System.out.println("║  3. Edit Internship                      ║");
        System.out.println("║  4. Delete Internship                    ║");
        System.out.println("║  5. View Applications                    ║");
        System.out.println("║  6. Manage Applications                  ║");
        System.out.println("║  7. Toggle Internship Visibility         ║");
        System.out.println("║  8. Change Password                      ║");
        System.out.println("║  9. Logout                               ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Select option: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        switch (choice) {
            case 1:
                createInternship(rep);
                break;
            case 2:
                viewMyInternships(rep);
                break;
            case 3:
                editInternship(rep);
                break;
            case 4:
                deleteInternship(rep);
                break;
            case 5:
                rep.viewApplications();
                break;
            case 6:
                manageApplications(rep);
                break;
            case 7:
                toggleVisibility(rep);
                break;
            case 8:
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case 9:
                logout();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    static void createInternship(CompanyRepresentative rep) {
        if (!rep.canCreateInternship()) {
            System.out.println("\nError: You have reached the maximum of 5 internships.");
            return;
        }

        System.out.println("\n=== Create Internship Opportunity ===");
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Level (Basic/Intermediate/Advanced): ");
        String level = scanner.nextLine().trim();
        System.out.print("Preferred Major: ");
        String major = scanner.nextLine().trim();
        System.out.print("Opening Date (yyyy-MM-dd): ");
        String openDateStr = scanner.nextLine().trim();
        System.out.print("Closing Date (yyyy-MM-dd): ");
        String closeDateStr = scanner.nextLine().trim();
        System.out.print("Number of Slots: ");
        String slotsStr = scanner.nextLine().trim();

        try {
            LocalDate openDate = LocalDate.parse(openDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate closeDate = LocalDate.parse(closeDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int slots = Integer.parseInt(slotsStr);

            // Validate slots (max 10)
            if (slots <= 0 || slots > 10) {
                System.out.println("Error: Number of slots must be between 1 and 10.");
                return;
            }

            Internship internship = new Internship(title, description, level, major,
                    openDate, closeDate, slots, rep);
            rep.createInternshipOpportunity(internship);
            allInternships.add(internship);
            saveAllData();

            System.out.println("\nInternship created! Waiting for Career Centre approval.");
        } catch (DateTimeParseException | NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        }
    }

    static void viewMyInternships(CompanyRepresentative rep) {
        List<Internship> myInternships = rep.getCreatedInternships();

        if (myInternships.isEmpty()) {
            System.out.println("\nYou have not created any internships yet.");
            return;
        }

        for (int i = 0; i < myInternships.size(); i++) {
            Internship internship = myInternships.get(i);
            System.out.println("\nInternship No " + (i + 1));
            internship.displayDetails();
        }
    }

    static void editInternship(CompanyRepresentative rep) {
        List<Internship> myInternships = rep.getCreatedInternships();
        if (myInternships.isEmpty()) {
            System.out.println("\nNo internships to edit.");
            return;
        }

        viewMyInternships(rep);
        System.out.print("\nEnter internship number to edit (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice < 1 || choice > myInternships.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Internship internship = myInternships.get(choice - 1);
            // Allow edits only if status is Pending
            if (!internship.getStatus().equals("Pending")) {
                System.out.println("\n✗ Cannot edit internship after staff approval.");
                return;
            }
            if (!rep.editInternship(internship)) {
                return;
            }

            System.out.println("\n=== Edit Internship ===");
            System.out.print("New Title (press Enter to keep current): ");
            String title = scanner.nextLine().trim();
            if (!title.isEmpty())
                internship.setTitle(title);

            System.out.print("New Description (press Enter to keep current): ");
            String desc = scanner.nextLine().trim();
            if (!desc.isEmpty())
                internship.setDescription(desc);

            saveAllData();
            System.out.println("\nInternship updated.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void deleteInternship(CompanyRepresentative rep) {
        List<Internship> myInternships = rep.getCreatedInternships();
        if (myInternships.isEmpty()) {
            System.out.println("\nNo internships to delete.");
            return;
        }

        viewMyInternships(rep);
        System.out.print("\nEnter internship number to delete (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice < 1 || choice > myInternships.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Internship internship = myInternships.get(choice - 1);
            if (rep.deleteInternship(internship)) {
                allInternships.remove(internship);
                saveAllData();
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void manageApplications(CompanyRepresentative rep) {
        List<Internship> myInternships = rep.getCreatedInternships();
        if (myInternships.isEmpty()) {
            System.out.println("\nNo internships created yet.");
            return;
        }

        // Select internship
        viewMyInternships(rep);
        System.out.print("\nSelect internship number: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > myInternships.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Internship internship = myInternships.get(choice - 1);
            List<Application> apps = internship.getApplications();

            if (apps.isEmpty()) {
                System.out.println("\nNo applications for this internship.");
                return;
            }

            // Display applications with ranking
            ApplicationViewer viewer = new ApplicationViewer(internship);
            viewer.rankApplicants();

            System.out.print("\nEnter application number to manage (0 to cancel): ");
            int appChoice = Integer.parseInt(scanner.nextLine().trim());
            if (appChoice == 0)
                return;

            List<Application> rankedApps = viewer.getRankedApplicants();
            if (rankedApps == null || rankedApps.isEmpty()) {
                System.out.println("No applications available to manage.");
                return;
            }
            if (appChoice < 1 || appChoice > rankedApps.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Application application = rankedApps.get(appChoice - 1);

            System.out.print("Approve or Reject? (A/R): ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("A")) {
                viewer.approveApplication(application);
                // Handle withdrawn / withdrawal pending applications
                if (application.getStatus() == Application.ApplicationStatus.WITHDRAWN) {
                    System.out.println("\nThis application has already been withdrawn and cannot be managed.");
                    return;
                }
                if (application.getWithdrawalStatus() == Application.WithdrawalStatus.PENDING) {
                    System.out.println(
                            "\nStudent has requested withdrawal for this application. Staff must process withdrawal requests.");
                    return;
                }
            } else if (action.equals("R")) {
                viewer.rejectApplication(application);
            } else {
                System.out.println("Invalid action.");
                return;
            }

            internship.checkFilledStatus();
            saveAllData();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void toggleVisibility(CompanyRepresentative rep) {
        List<Internship> myInternships = rep.getCreatedInternships();
        if (myInternships.isEmpty()) {
            System.out.println("\nNo internships to manage.");
            return;
        }

        viewMyInternships(rep);
        System.out.print("\nEnter internship number to toggle visibility (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice < 1 || choice > myInternships.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Internship internship = myInternships.get(choice - 1);
            rep.changeVisibility(internship, !internship.isVisible());
            saveAllData();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    // ==================== STAFF MENU ====================

    static void showStaffMenu() {
        CareerCentreStaff staff = (CareerCentreStaff) currentUser;

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║       CAREER CENTRE STAFF MENU           ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Authorize Company Representatives    ║");
        System.out.println("║  2. Approve/Reject Internships           ║");
        System.out.println("║  3. Approve Withdrawal Requests          ║");
        System.out.println("║  4. Generate Reports                     ║");
        System.out.println("║  5. View All Internships                 ║");
        System.out.println("║  6. Change Password                      ║");
        System.out.println("║  7. Logout                               ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Select option: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        switch (choice) {
            case 1:
                authorizeCompanyReps(staff);
                break;
            case 2:
                approveInternships(staff);
                break;
            case 3:
                manageWithdrawalRequests(staff);
                break;
            case 4:
                generateReports(staff);
                break;
            case 5:
                viewAllInternships();
                break;
            case 6:
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case 7:
                logout();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    static void authorizeCompanyReps(CareerCentreStaff staff) {
        if (pendingCompanyReps.isEmpty()) {
            System.out.println("\nNo pending company representative registrations.");
            return;
        }

        System.out.println("\n=== Pending Company Representatives ===");
        for (int i = 0; i < pendingCompanyReps.size(); i++) {
            CompanyRepresentative rep = pendingCompanyReps.get(i);
            System.out.println((i + 1) + ". " + rep.getName() + " - " + rep.getCompanyName() +
                    " (" + rep.getEmail() + ")");
        }

        System.out.print("\nEnter number to authorize (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice < 1 || choice > pendingCompanyReps.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            CompanyRepresentative rep = pendingCompanyReps.get(choice - 1);
            System.out.print("Approve or Reject? (A/R): ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("A")) {
                rep.setStatus("APPROVED");
                registeredAccounts.put(rep.getUserID(), rep);
                pendingCompanyReps.remove(choice - 1);
                System.out.println("Company representative approved.");
            } else if (action.equals("R")) {
                pendingCompanyReps.remove(choice - 1);
                System.out.println("Company representative rejected.");
            }

            saveAllData();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void approveInternships(CareerCentreStaff staff) {
        List<Internship> pending = allInternships.stream()
                .filter(i -> "Pending".equals(i.getStatus()))
                .collect(Collectors.toList());

        if (pending.isEmpty()) {
            System.out.println("\nNo pending internships to approve.");
            return;
        }

        System.out.println("\n=== Pending Internships ===");
        for (int i = 0; i < pending.size(); i++) {
            Internship internship = pending.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle() +
                    " - "
                    + (internship.getCompanyRepresentative() != null
                            ? internship.getCompanyRepresentative().getCompanyName()
                            : "N/A"));
        }

        System.out.print("\nEnter number to manage (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice < 1 || choice > pending.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Internship internship = pending.get(choice - 1);
            internship.displayDetails();

            System.out.print("\nApprove or Reject? (A/R): ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("A")) {
                staff.approveInternshipOpportunity(internship);
            } else if (action.equals("R")) {
                internship.setStatus("Rejected");
                System.out.println("Internship rejected.");
            }

            saveAllData();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void manageWithdrawalRequests(CareerCentreStaff staff) {
        List<Application> pending = allInternships.stream()
                .flatMap(i -> i.getApplications().stream())
                .filter(a -> a.getWithdrawalStatus() == Application.WithdrawalStatus.PENDING)
                .collect(Collectors.toList());

        if (pending.isEmpty()) {
            System.out.println("\nNo pending withdrawals to approve.");
            return;
        }

        System.out.println("\n=== Pending Withdrawals ===");
        for (int i = 0; i < pending.size(); i++) {
            Application a = pending.get(i);
            System.out.println("\n[" + (i + 1) + "] Internship: " + a.getInternship().getTitle());
            System.out.println("    Student: " + a.getStudent().getName() + " (" + a.getStudent().getUserID() + ")");
            System.out.println("    Status: " + a.getStatus());
            System.out.println("    Placement Status: " + a.isConfirmed());
        }

        System.out.print("\nSelect request number to process (0 to cancel): ");
        try {
            int sel = Integer.parseInt(scanner.nextLine().trim());
            if (sel == 0)
                return;
            if (sel < 1 || sel > pending.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Application app = pending.get(sel - 1);
            System.out.print("Approve or Reject? (A/R): ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("A")) {
                if (staff.approveWithdrawal(app)) {
                    System.out.println("Withdrawal approved; application marked as Withdrawn.");
                } else {
                    System.out.println("Unable to approve withdrawal (not pending).");
                }
            } else if (action.equals("R")) {
                if (staff.rejectWithdrawal(app)) {
                    System.out.println("Withdrawal request rejected.");
                } else {
                    System.out.println("Unable to reject withdrawal (not pending).");
                }
            } else {
                System.out.println("Invalid action.");
                return;
            }

            // If approving a withdrawal may free a slot or affect internship state, handle
            // here.
            saveAllData();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void generateReports(CareerCentreStaff staff) {
        System.out.println("\n=== Generate Report ===");
        System.out.println("1. Filter by Status");
        System.out.println("2. Filter by Level");
        System.out.println("3. Filter by Preferred Major");
        System.out.print("Select filter: ");

        int choice;   // default so it compiles
        int filterType;
        String filter;

        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        switch (choice) {
            case 1:
                System.out.println("Please enter 1 of the following (Pending / Approved / Rejected / Filled)");
                filter = scanner.nextLine().trim().toLowerCase(); // Use nextLine() for consistency
                if (!filter.equals("pending") &&
                        !filter.equals("approved") &&
                        !filter.equals("rejected") &&
                        !filter.equals("filled")) {

                    System.out.println("Invalid status!");
                    return;
                }
                filterType = 1;
                break;
            case 2:
                System.out.println("Please enter 1 of the following (Basic / Intermediate / Advanced)");
                filter = scanner.nextLine().trim().toLowerCase(); 
                if (!filter.equals("basic") && !filter.equals("intermediate") && !filter.equals("advanced")) {
                    System.out.println("Invalid level!");
                    return;
                }
                filterType = 2;
                break;
            case 3:
                System.out.println("Please enter the major");
                filter = scanner.nextLine().trim().toLowerCase(); // Use nextLine() for possible multi-word majors
                filterType = 3;
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        staff.generateReport(allInternships, filter, filterType);
    }

    static void viewAllInternships() {
        if (allInternships.isEmpty()) {
            System.out.println("\nNo internships in the system.");
            return;
        }

        System.out.println("\n=== All Internships ===");
        for (Internship internship : allInternships) {
            internship.displayDetails();
            System.out.println("---");
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
