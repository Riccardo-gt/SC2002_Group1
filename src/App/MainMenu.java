package App;

import Models.Entities.*;
import Models.Utility_Classes.*;
import java.util.*;

/**
 * Handles main menu operations (login and registration)
 * Single Responsibility: Main menu display and initial user actions
 */
public class MainMenu {
    private Scanner scanner;
    private HashMap<String, User> registeredAccounts;
    private List<CompanyRepresentative> pendingCompanyReps;

    public MainMenu(Scanner scanner, HashMap<String, User> registeredAccounts,
                    List<CompanyRepresentative> pendingCompanyReps) {
        this.scanner = scanner;
        this.registeredAccounts = registeredAccounts;
        this.pendingCompanyReps = pendingCompanyReps;
    }

    /**
     * Display main menu and get user choice
     * @return User choice (1-3)
     */
    public int displayMenu() {
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
            return -1;
        }
        return choice;
    }

    /**
     * Handle login process
     * @return Logged in user or null if login failed
     */
    public User login() {
        System.out.print("\nEnter User ID: ");
        String userId = scanner.nextLine().trim();
        User user = registeredAccounts.get(userId);
        if (user == null) {
            System.out.println("\nInvalid username. Please try again.");
            return null;
        }
        System.out.print("Enter Password or click 1 if you forgot your password and wish to reset it: ");
        String password = scanner.nextLine().trim();
        if (password.equals("1")) {
            user.resetPassword(scanner);
            return null;
        }
        else if (!user.login(password)) {
            System.out.println("\nInvalid password. Please try again.");
            return null;
        }
        System.out.println("\nLogin successful! Welcome, " + user.getName());
        return user;
    }

    /**
     * Handle company representative registration
     */
    public void registerCompanyRep() {
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

        System.out.println("\nRegistration submitted! Please wait for Career Centre Staff approval.");
    }
}
