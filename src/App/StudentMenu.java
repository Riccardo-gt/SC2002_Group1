package App;

import Models.Entities.*;
import Models.Utility_Classes.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles student menu operations
 * Single Responsibility: Student menu display and student-specific actions
 */
public class StudentMenu {
    private Scanner scanner;
    private List<Internship> allInternships;

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