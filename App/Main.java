import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

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
        registerStudents();
        registerStaff();
        loadCompanyReps();
        loadInternships();
        loadApplications();
        System.out.println("System ready!\n");
    }

    static void registerStudents() {
        try {
            BufferedReader file = new BufferedReader(new FileReader("Datasets/sample_student_list.csv"));
            String line = file.readLine(); // Read the header

            while ((line = file.readLine()) != null) {
                String[] parts = line.split(",");
                // Format: UserID,Name,Major,Year,Email
                Student student = new Student(parts[0], parts[1], parts[4], "password", parts[2], Integer.valueOf(parts[3]));
                registeredAccounts.put(parts[0], student);
            }
            file.close();
            System.out.println("✓ Students loaded");
        } catch (IOException error) {
            System.out.println("Note: sample_student_list.csv not found. Starting with no students.");
        }
    }

    static void registerStaff() {
        try {
            BufferedReader file = new BufferedReader(new FileReader("Datasets/sample_staff_list.csv"));
            String line = file.readLine();

            while ((line = file.readLine()) != null) {
                String[] parts = line.split(",");
                // Format: UserID,Name,Role,Department,Email
                CareerCentreStaff staff = new CareerCentreStaff(parts[0], parts[1], parts[4], "password", parts[2], parts[3]);
                registeredAccounts.put(parts[0], staff);
            }
            file.close();
            System.out.println("✓ Staff loaded");
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
        System.out.println("✓ Company representatives loaded");
    }

    static void loadInternships() {
        allInternships = FileIOHandler.loadInternships(registeredAccounts);
        System.out.println("✓ Internships loaded");
    }

    static void loadApplications() {
        FileIOHandler.loadApplications(allInternships, registeredAccounts);
        System.out.println("✓ Applications loaded");
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

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                registerCompanyRep();
                break;
            case "3":
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
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = registeredAccounts.get(userId);
        if (user != null && user.login(password)) {
            currentUser = user;
            System.out.println("\n✓ Login successful! Welcome, " + user.getName());
        } else {
            System.out.println("\n✗ Invalid credentials. Please try again.");
        }
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
                email, name, email, password, company, position, department, "PENDING"
        );

        pendingCompanyReps.add(rep);
        saveAllData();

        System.out.println("\n✓ Registration submitted! Please wait for Career Centre Staff approval.");
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

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                viewAvailableInternshipsForStudent(student);
                break;
            case "2":
                applyForInternship(student);
                break;
            case "3":
                student.viewAppliedInternships();
                break;
            case "4":
                acceptPlacement(student);
                break;
            case "5":
                requestWithdrawal(student);
                break;
            case "6":
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case "7":
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
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println("\nNo internships available for your profile.");
            return;
        }

        System.out.println("\n=== Available Internships ===");
        for (int i = 0; i < available.size(); i++) {
            Internship internship = available.get(i);
            System.out.println("\n[" + (i + 1) + "] " + internship.getTitle());
            System.out.println("    Company: " + (internship.getCompanyRepresentative() != null ?
                    internship.getCompanyRepresentative().getCompanyName() : "N/A"));
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
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println("\nNo internships available.");
            return;
        }

        viewAvailableInternshipsForStudent(student);
        System.out.print("\nEnter internship number to apply (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
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
        List<Application> approved = student.getApplications().stream()
                .filter(app -> app.getStatus() == Application.ApplicationStatus.APPROVED)
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
            if (choice == 0) return;
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
            if (choice == 0) return;
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

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                createInternship(rep);
                break;
            case "2":
                viewMyInternships(rep);
                break;
            case "3":
                editInternship(rep);
                break;
            case "4":
                deleteInternship(rep);
                break;
            case "5":
                rep.viewApplications();
                break;
            case "6":
                manageApplications(rep);
                break;
            case "7":
                toggleVisibility(rep);
                break;
            case "8":
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case "9":
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

            Internship internship = new Internship(title, description, level, major,
                    openDate, closeDate, slots, rep);
            rep.createInternshipOpportunity(internship);
            allInternships.add(internship);
            saveAllData();

            System.out.println("\n✓ Internship created! Waiting for Career Centre approval.");
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

        System.out.println("\n=== My Internships ===");
        for (int i = 0; i < myInternships.size(); i++) {
            Internship internship = myInternships.get(i);
            System.out.println("\n[" + (i + 1) + "] " + internship.getTitle());
            System.out.println("    Status: " + internship.getStatus());
            System.out.println("    Visible: " + (internship.isVisible() ? "Yes" : "No"));
            System.out.println("    Level: " + internship.getLevel());
            System.out.println("    Slots: " + internship.getSlots());
            System.out.println("    Applications: " + internship.getApplications().size());
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
            if (choice == 0) return;
            if (choice < 1 || choice > myInternships.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Internship internship = myInternships.get(choice - 1);
            if (!rep.editInternship(internship)) {
                return;
            }

            System.out.println("\n=== Edit Internship ===");
            System.out.print("New Title (press Enter to keep current): ");
            String title = scanner.nextLine().trim();
            if (!title.isEmpty()) internship.setTitle(title);

            System.out.print("New Description (press Enter to keep current): ");
            String desc = scanner.nextLine().trim();
            if (!desc.isEmpty()) internship.setDescription(desc);

            saveAllData();
            System.out.println("\n✓ Internship updated.");

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
            if (choice == 0) return;
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
            if (appChoice == 0) return;
            if (appChoice < 1 || appChoice > apps.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Application application = apps.get(appChoice - 1);
            System.out.print("Approve or Reject? (A/R): ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("A")) {
                viewer.approveApplication(application);
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
            if (choice == 0) return;
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

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                authorizeCompanyReps(staff);
                break;
            case "2":
                approveInternships(staff);
                break;
            case "3":
                approveWithdrawals(staff);
                break;
            case "4":
                generateReports(staff);
                break;
            case "5":
                viewAllInternships();
                break;
            case "6":
                currentUser.changePassword(scanner);
                saveAllData();
                break;
            case "7":
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
            if (choice == 0) return;
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
                System.out.println("✓ Company representative approved.");
            } else if (action.equals("R")) {
                pendingCompanyReps.remove(choice - 1);
                System.out.println("✓ Company representative rejected.");
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
                    " - " + (internship.getCompanyRepresentative() != null ?
                    internship.getCompanyRepresentative().getCompanyName() : "N/A"));
        }

        System.out.print("\nEnter number to manage (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
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
                System.out.println("✓ Internship rejected.");
            }

            saveAllData();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    static void approveWithdrawals(CareerCentreStaff staff) {
        System.out.println("\n=== Withdrawal Requests ===");
        System.out.println("(Feature requires tracking withdrawal requests separately)");
        System.out.println("Current implementation: Students can request, staff manually approve.");
    }

    static void generateReports(CareerCentreStaff staff) {
        System.out.println("\n=== Generate Report ===");
        System.out.println("1. All Internships");
        System.out.println("2. Filter by Status");
        System.out.println("3. Filter by Level");
        System.out.println("4. Filter by Major");
        System.out.print("Select filter: ");

        String choice = scanner.nextLine().trim();
        List<Internship> filtered = new ArrayList<>(allInternships);

        switch (choice) {
            case "1":
                // All internships
                break;
            case "2":
                System.out.print("Enter status (Pending/Approved/Rejected/Filled): ");
                String status = scanner.nextLine().trim();
                filtered = allInternships.stream()
                        .filter(i -> status.equalsIgnoreCase(i.getStatus()))
                        .collect(Collectors.toList());
                break;
            case "3":
                System.out.print("Enter level (Basic/Intermediate/Advanced): ");
                String level = scanner.nextLine().trim();
                filtered = allInternships.stream()
                        .filter(i -> level.equalsIgnoreCase(i.getLevel()))
                        .collect(Collectors.toList());
                break;
            case "4":
                System.out.print("Enter major: ");
                String major = scanner.nextLine().trim();
                filtered = allInternships.stream()
                        .filter(i -> major.equalsIgnoreCase(i.getPreferredMajor()))
                        .collect(Collectors.toList());
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        staff.generateReport(filtered);
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