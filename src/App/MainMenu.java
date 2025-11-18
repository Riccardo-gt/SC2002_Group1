package App;

import Models.Entities.*;
import Models.Utility_Classes.*;
import java.util.*;

/**
 * Handles main menu operations including login and company representative registration.
 * This class follows the Single Responsibility Principle by focusing solely on
 * main menu display and initial user authentication actions.
 *
 * <p>The main menu provides three primary functions:</p>
 * <ul>
 *   <li>User login with password recovery option</li>
 *   <li>Company representative registration (requires staff approval)</li>
 *   <li>Exit the system</li>
 * </ul>
 *
 * <p>This class acts as the gateway to the system, validating credentials
 * and routing users to their appropriate role-based menus after successful login.</p>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public class MainMenu {
    private Scanner scanner;
    private HashMap<String, User> registeredAccounts;
    private List<CompanyRepresentative> pendingCompanyReps;

    /**
     * Constructs a new MainMenu with the specified dependencies.
     *
     * @param scanner Scanner instance for reading user input
     * @param registeredAccounts map of all registered user accounts
     * @param pendingCompanyReps list of company representatives awaiting approval
     */
    public MainMenu(Scanner scanner, HashMap<String, User> registeredAccounts,
                    List<CompanyRepresentative> pendingCompanyReps) {
        this.scanner = scanner;
        this.registeredAccounts = registeredAccounts;
        this.pendingCompanyReps = pendingCompanyReps;
    }

    /**
     * Displays the main menu and prompts the user for their choice.
     * The menu presents options for login, registration, and exit.
     *
     * @return the user's menu choice (1-3), or -1 if invalid input
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
     * Handles the login process for existing users.
     *
     * <p>Login process:</p>
     * <ol>
     *   <li>Prompts for user ID and validates account existence</li>
     *   <li>Prompts for password or password reset option (enter '1')</li>
     *   <li>Validates password against stored credentials</li>
     *   <li>Returns the User object on successful login</li>
     * </ol>
     *
     * <p>Password reset option allows users to recover their account
     * without knowing the current password.</p>
     *
     * @return the logged-in User object if successful, null if login failed or password was reset
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
     * Handles company representative registration.
     *
     * <p>Registration process:</p>
     * <ol>
     *   <li>Validates email format (must contain '@')</li>
     *   <li>Checks for duplicate email addresses</li>
     *   <li>Collects user information (name, password, company details)</li>
     *   <li>Validates password against complexity requirements</li>
     *   <li>Creates a new CompanyRepresentative with PENDING status</li>
     *   <li>Adds to pending list for Career Centre Staff approval</li>
     * </ol>
     *
     * <p>Password requirements:</p>
     * <ul>
     *   <li>At least one uppercase letter</li>
     *   <li>At least one lowercase letter</li>
     *   <li>At least one digit</li>
     *   <li>At least one special character (!@#$%)</li>
     * </ul>
     *
     * <p>Note: Email address serves as the user ID for company representatives.</p>
     */
    public void registerCompanyRep() {
        System.out.println("\n=== Company Representative Registration ===");
        System.out.print("Email (will be your User ID): ");
        String email = scanner.nextLine().trim();

        if (!email.contains("@")) {
            System.out.println("Invalid email format.");
            return;
        }

        if (registeredAccounts.containsKey(email)) {
            System.out.println("Error: This email is already registered.");
            return;
        }

        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        while (User.isPasswordValid(password) == false) {
            System.out.println("Password does not meet complexity requirements. Please re-enter a valid password:");
            password = scanner.nextLine();
        }
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
