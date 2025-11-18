package App;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import Models.Entities.*;
import Models.Utility_Classes.*;

/**
 * Handles all menu display and operation logic specific to the
 * {@link CompanyRepresentative} role.
 * This class provides methods for creating, viewing, editing, deleting internships,
 * and managing applications.
 */
public class CompanyRepMenu {
    private Scanner scanner;
    private List<Internship> allInternships;

    /**
     * Constructs a new CompanyRepMenu instance.
     *
     * @param scanner The {@code Scanner} instance to use for input reading.
     * @param allInternships A list containing all system {@link Internship} objects.
     */
    public CompanyRepMenu(Scanner scanner, List<Internship> allInternships) {
        this.scanner = scanner;
        this.allInternships = allInternships;
    }

    /**
     * Display company representative menu and get user choice
     * @return User choice (1-9)
     */
    public int displayMenu() {
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
            return -1;
        }
        return choice;
    }

    /**
     * Guides the representative through the process of creating a new internship.
     * Collects all required details (title, dates, slots, etc.) and validates input formats.
     * The new internship is added to the representative's list and the global list.
     *
     * @param rep The currently logged-in {@link CompanyRepresentative}.
     */
    public void createInternship(CompanyRepresentative rep) {
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

            System.out.println("\nInternship created! Waiting for Career Centre approval.");
        } catch (DateTimeParseException | NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        }
    }

    /**
     * Displays a list of all internships created by the representative, optionally allowing
     * the user to filter the list by status before display.
     * It uses the inherited {@code filterInternships} and {@code displayInternships} methods.
     *
     * @param rep The currently logged-in {@link CompanyRepresentative}.
     */
    public void viewMyInternships(CompanyRepresentative rep) {
    List<Internship> myInternships = rep.getCreatedInternships();

    if (myInternships.isEmpty()) {
        System.out.println("\nYou have not created any internships yet.");
        return;
    }

    // Ask if they want to filter
    System.out.println("\nYou have " + myInternships.size() + " internships.");
    System.out.print("Filter by status? (y/n): ");
    String choice = scanner.nextLine().trim().toLowerCase();
    
    List<Internship> displayList = myInternships;
    if (choice.equals("y") || choice.equals("yes")) {
        System.out.print("Enter status (Pending/Approved/Rejected/Filled): ");
        String status = scanner.nextLine().trim();
        displayList = rep.filterInternships(myInternships, "status", status);
    }

    // Use the inherited display method from User class
    rep.displayInternships(displayList, "Your Internships");
}

    /**
     * Allows the representative to modify the details of an existing internship.
     * Edits are only permitted if the internship status is "Pending" (before staff approval).
     *
     * @param rep The currently logged-in {@link CompanyRepresentative}.
     */
    public void editInternship(CompanyRepresentative rep) {
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

            System.out.print("New Level (Basic/Intermediate/Advanced) (press Enter to keep '" + internship.getLevel() + "'): ");
            String level = scanner.nextLine().trim();
            if (!level.isEmpty()) {
                if (level.equalsIgnoreCase("Basic") || level.equalsIgnoreCase("Intermediate") || level.equalsIgnoreCase("Advanced")) {
                internship.setLevel(level);
                } else {
                    System.out.println("Invalid level. Must be Basic, Intermediate, or Advanced. Keeping current level.");
                }
            }
            
            System.out.print("New Preferred Major (press Enter to keep '" + internship.getPreferredMajor() + "'): ");
            String major = scanner.nextLine().trim();
            if (!major.isEmpty()) {
                internship.setPreferredMajor(major);
            }

            System.out.print("New Number of Slots (press Enter to keep '" + internship.getSlots() + "'): ");
            String slotsStr = scanner.nextLine().trim();
            if (!slotsStr.isEmpty()) {
                try {
                    int slots = Integer.parseInt(slotsStr);
                    if (slots > 0 && slots <= 10) {
                        internship.setSlots(slots);
                    } else {
                        System.out.println("Slots must be between 1 and 10. Keeping current slots.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Keeping current slots.");
                }
            }
            
            System.out.println("\nInternship updated.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Allows the representative to delete an internship opportunity.
     * Deletion is prevented if the internship has been approved by the Career Centre staff.
     *
     * @param rep The currently logged-in {@link CompanyRepresentative}.
     */
    public void deleteInternship(CompanyRepresentative rep) {
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
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Allows the representative to select an internship, view its ranked applications,
     * and then approve or reject a specific {@link Application}.
     * Status changes are delegated to the {@link ApplicationViewer}.
     *
     * @param rep The currently logged-in {@link CompanyRepresentative}.
     */
    public void manageApplications(CompanyRepresentative rep) {
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

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Toggles the visibility status of a selected internship.
     * The operation is delegated to the {@link CompanyRepresentative}'s {@code changeVisibility} method.
     *
     * @param rep The currently logged-in {@link CompanyRepresentative}.
     */
    public void toggleVisibility(CompanyRepresentative rep) {
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

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
}
