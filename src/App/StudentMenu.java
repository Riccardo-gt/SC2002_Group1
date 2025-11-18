package App;

import Models.Entities.*;
import Models.Utility_Classes.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all menu display and operation logic specific to the
 * {@link Student} role.
 * This class provides methods for viewing, applying for, and managing applications
 * for internship opportunities.
 *
 * @see Student
 * @see Internship
 * @see Application
 */
public class StudentMenu {
    private Scanner scanner;
    private List<Internship> allInternships;

    /**
     * Constructs a new StudentMenu instance.
     *
     * @param scanner The {@code Scanner} instance to use for input reading.
     * @param allInternships A list containing all system {@link Internship} objects.
     */
    public StudentMenu(Scanner scanner, List<Internship> allInternships) {
        this.scanner = scanner;
        this.allInternships = allInternships;
    }

    /**
     * Display student menu and get user choice
     * @return User choice (1-7)
     */
    public int displayMenu() {
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
            return -1;
        }
        return choice;
    }

    /**
     * Filters the global list of internships to display those that are currently accepting
     * applications and for which the student is eligible (based on level and preferred major).
     * Allows the student to apply additional filters (level, company) afterwards.
     *
     * @param student The currently logged-in {@link Student}.
     */
    public void viewAvailableInternships(Student student) {
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

    // Ask if user wants to filter
    System.out.println("\nFound " + available.size() + " internships for your profile.");
    System.out.print("Do you want to filter results? (y/n): ");
    String choice = scanner.nextLine().trim().toLowerCase();
    
    if (choice.equals("y") || choice.equals("yes")) {
        available = applyAdditionalFilters(student, available);
    }

    
    student.displayInternships(available, "Available Internships");
}

    /**
     * Applies additional filters (Level or Company) to a provided list of internships based on user input.
     *
     * @param student The currently logged-in {@link Student}.
     * @param internships The list of internships to filter.
     * @return The filtered list of internships.
     */
    private List<Internship> applyAdditionalFilters(Student student, List<Internship> internships) {
        System.out.println("\n=== Additional Filters ===");
        System.out.println("1. Filter by Level");
        System.out.println("2. Filter by Company");
        System.out.println("3. No additional filters");
        System.out.print("Select option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    System.out.print("Enter level (Basic/Intermediate/Advanced): ");
                    String level = scanner.nextLine().trim();
                    return student.filterInternships(internships, "level", level);
                case 2:
                    System.out.print("Enter company name: ");
                    String company = scanner.nextLine().trim();
                    return student.filterInternships(internships, "company", company);
                case 3:
                    return internships;
                default:
                    System.out.println("Invalid choice. Showing all results.");
                    return internships;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Showing all results.");
            return internships;
        }
    }

    /**
     * Presents the student with a list of eligible internships they have not yet applied to,
     * and prompts the student to select one for application.
     * The application submission logic is delegated to {@link Student#applyForInternship}.
     *
     * @param student The currently logged-in {@link Student}.
     */
    public void applyForInternship(Student student) {
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
            student.applyForInternship(internship);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Allows the student to view and accept an internship offer.
     * Acceptance is only possible if the student has not already confirmed a placement
     * and the application status is {@link Application.ApplicationStatus#SUCCESSFUL} and not yet confirmed.
     * The confirmation logic is delegated to {@link Student#acceptPlacement}.
     *
     * @param student The currently logged-in {@link Student}.
     */
    public void acceptPlacement(Student student) {
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

            student.acceptPlacement(approved.get(choice - 1));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Guides the student through selecting one of their submitted applications to request a withdrawal.
     * The request logic is delegated to {@link Student#requestWithdrawal}.
     *
     * @param student The currently logged-in {@link Student}.
     */
    public void requestWithdrawal(Student student) {
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
}
