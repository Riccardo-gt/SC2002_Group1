package App;

import Models.Entities.*;
import Models.Utility_Classes.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all menu display and operation logic specific to the
 * {@link CareerCentreStaff} role.
 * This class provides methods for administrative functions such as authorizing
 * company accounts, approving internships, managing withdrawals, and generating reports.
 *
 * @see CareerCentreStaff
 * @see Internship
 * @see CompanyRepresentative
 * @see Application
 */
public class StaffMenu {
    private Scanner scanner;
    private List<Internship> allInternships;
    private List<CompanyRepresentative> pendingCompanyReps;
    private HashMap<String, User> registeredAccounts;

    /**
     * Constructs a new StaffMenu instance.
     *
     * @param scanner The {@code Scanner} instance to use for input reading.
     * @param allInternships A list containing all system {@link Internship} objects.
     * @param pendingCompanyReps A list of {@link CompanyRepresentative} accounts requiring authorization.
     * @param registeredAccounts A map holding all active {@link User} accounts.
     */
    public StaffMenu(Scanner scanner, List<Internship> allInternships,
                     List<CompanyRepresentative> pendingCompanyReps,
                     HashMap<String, User> registeredAccounts) {
        this.scanner = scanner;
        this.allInternships = allInternships;
        this.pendingCompanyReps = pendingCompanyReps;
        this.registeredAccounts = registeredAccounts;
    }

    /**
     * Display staff menu and get user choice
     * @return User choice (1-7)
     */
    public int displayMenu() {
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
            return -1;
        }
        return choice;
    }

    /**
     * Manages the authorization process for new {@link CompanyRepresentative} accounts.
     * Staff can approve or reject pending representatives. Approved accounts are moved
     * from the pending list to the registered accounts map.
     *
     * @param staff The currently logged-in {@link CareerCentreStaff} member.
     */
    public void authorizeCompanyReps(CareerCentreStaff staff) {
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

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Manages the approval process for new {@link Internship} opportunities.
     * Staff can view pending internships and choose to approve (making them visible)
     * or reject them.
     *
     * @param staff The currently logged-in {@link CareerCentreStaff} member.
     */
    public void approveInternships(CareerCentreStaff staff) {
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

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Manages student withdrawal requests for applications.
     * Staff can view all applications with a {@link Application.WithdrawalStatus#PENDING}
     * and choose to approve (marking the application as {@link Application.ApplicationStatus#WITHDRAWN})
     * or reject the request.
     *
     * @param staff The currently logged-in {@link CareerCentreStaff} member.
     */
    public void manageWithdrawalRequests(CareerCentreStaff staff) {
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

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Guides the staff member through filtering internships and generating a report.
     * This method collects the filter type (Status, Level, or Major) and the filter value,
     * then delegates the actual filtering and display to {@link CareerCentreStaff#generateReport}.
     *
     * @param staff The currently logged-in {@link CareerCentreStaff} member.
     */
    public void generateReports(CareerCentreStaff staff) {
        System.out.println("\n=== Generate Report ===");
        System.out.println("1. Filter by Status");
        System.out.println("2. Filter by Level");
        System.out.println("3. Filter by Preferred Major");
        System.out.print("Select filter: ");

        int choice;
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
                filter = scanner.nextLine().trim().toLowerCase();
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
                filter = scanner.nextLine().trim().toLowerCase();
                filterType = 3;
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        staff.generateReport(allInternships, filter, filterType);
    }

    /**
     * Displays details for all internships currently registered in the system.
     */
    public void viewAllInternships() {
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
}