package Models.Entities;

import Models.Utility_Classes.FileIOHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract base class representing a user in the Internship Placement Management System.
 * All users (Student, CompanyRepresentative, CareerCentreStaff) inherit from this class.
 * Provides common functionality for authentication, password management, and internship filtering.
 *
 * <p>This class implements encapsulation by keeping all attributes private and providing
 * controlled access through public methods.</p>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public abstract class User {
    // Encapsulation: All attributes are private 
    private String userID;
    private String name;
    private String email;
    private String password;

    /**
     * Constructs a new User with the specified details.
     *
     * @param userID unique identifier for the user
     * @param name full name of the user
     * @param email email address of the user
     * @param password password for authentication
     */
    public User(String userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password; // Default password = "password"
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password; 
    }

    public String getEmail() { return email; }

    /**
     * Authenticates the user by comparing the entered password with the stored password.
     *
     * @param enteredPassword the password entered by the user
     * @return true if the entered password matches the stored password, false otherwise
     */
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword); // Use hashing
    }

    /**
     * Logs out the user and displays a confirmation message.
     */
    public void logout() {
        System.out.println(this.name + " (" + this.userID + ") has been logged out.");
    }

    /**
     * Resets the user's password without requiring the current password.
     * Used for password recovery. Validates the new password against complexity requirements
     * and persists the change to the CSV file.
     *
     * @param scanner Scanner object for reading user input
     */
    public void resetPassword(Scanner scanner) {
        // 2. Input New Password
        System.out.println("Enter new password (Ensure it has at least 1 Uppercase, 1 Lowercase, 1 special character (!@#$%), and 1 number):");
        String newPassword = scanner.nextLine();

        // 3. Validate and Update
        while (!isPasswordValid(newPassword)) {
            System.out.println("Password does not meet complexity requirements. Please re-enter a valid password:");
            newPassword = scanner.nextLine();
        }

        this.password = newPassword;

        // Save the password change to CSV file
        if (FileIOHandler.updateUserPassword(this)) {
            System.out.println("Password successfully updated and saved!");
        } else {
            System.out.println("Password updated in current session, but failed to save to file.");
            System.out.println("Your password change may not persist after logout.");
        }
    }

    /**
     * Changes the user's password after verifying the current password.
     * Validates the new password against complexity requirements and persists
     * the change to the CSV file.
     *
     * @param scanner Scanner object for reading user input
     */
    public void changePassword(Scanner scanner) {
        System.out.println("--- Change Password ---");
        String currentPassword;
        String newPassword;

        // 1. Verify Current Password
        System.out.print("Enter current password: ");
        currentPassword = scanner.nextLine().trim();

        if (!currentPassword.equals(this.password)) {
            System.out.println("Error: Current password incorrect. Password change aborted.");
            return;
        }

        // 2. Input New Password
        System.out.println("Enter new password (Ensure it has at least 1 Uppercase, 1 Lowercase, 1 special character (!@#$%), and 1 number):");
        newPassword = scanner.nextLine();

        // 3. Validate and Update
        while (!isPasswordValid(newPassword)) {
            System.out.println("Password does not meet complexity requirements. Please re-enter a valid password:");
            newPassword = scanner.nextLine();
        }

        this.password = newPassword;

        // Save the password change to CSV file
        if (FileIOHandler.updateUserPassword(this)) {
            System.out.println("Password successfully updated and saved!");
        } else {
            System.out.println("Password updated in current session, but failed to save to file.");
            System.out.println("Your password change may not persist after logout.");
        }
    }

    /**
     * Validates a password against complexity requirements.
     * A valid password must contain:
     * <ul>
     *   <li>At least one uppercase letter</li>
     *   <li>At least one lowercase letter</li>
     *   <li>At least one digit</li>
     *   <li>At least one special character (!@#$%)</li>
     *   <li>Only allowed characters (letters, digits, and !@#$%)</li>
     * </ul>
     *
     * @param password the password to validate
     * @return true if the password meets all complexity requirements, false otherwise
     */
    public static boolean isPasswordValid(String password) {
        final int LENGTH = password.length();
        boolean foundLower = false, foundUpper = false, foundSpecial = false, foundNumber = false;
        final HashSet<Character> SPECIAL = new HashSet<Character>(Arrays.asList('!', '@', '#', '$', '%'));

        for (int i = 0; i < LENGTH; i++) {
            char current = password.charAt(i);
            if (Character.isAlphabetic(current)) {
                if (Character.isLowerCase(current)) {
                    foundLower = true;
                }
                else {
                    foundUpper = true;
                }
            }
            else if (Character.isDigit(current)) {
                foundNumber = true;
            }
            else if (SPECIAL.contains(current)) {
                foundSpecial = true;
            }
            else {
                return false;
            }
        }
        return foundLower && foundUpper && foundSpecial && foundNumber;
    }

    /**
     * Filters a list of internships based on the specified criteria.
     * Supported filter types include:
     * <ul>
     *   <li>status - filters by internship status (Pending, Approved, Rejected, Filled)</li>
     *   <li>level - filters by difficulty level (Basic, Intermediate, Advanced)</li>
     *   <li>major - filters by preferred major</li>
     *   <li>company - filters by company name</li>
     *   <li>visible - filters by visibility status</li>
     * </ul>
     *
     * @param internships the list of internships to filter
     * @param filterType the type of filter to apply
     * @param filterValue the value to filter by
     * @return a filtered list of internships matching the criteria
     */
    public List<Internship> filterInternships(List<Internship> internships, String filterType, String filterValue) {
        if (internships == null || internships.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Internship> filtered = new ArrayList<>();
        
        switch (filterType.toLowerCase()) {
            case "status":
                filtered = internships.stream()
                        .filter(i -> i.getStatus().equalsIgnoreCase(filterValue))
                        .collect(Collectors.toList());
                break;
                
            case "level":
                filtered = internships.stream()
                        .filter(i -> i.getLevel().equalsIgnoreCase(filterValue))
                        .collect(Collectors.toList());
                break;
                
            case "major":
                filtered = internships.stream()
                        .filter(i -> i.getPreferredMajor().equalsIgnoreCase(filterValue))
                        .collect(Collectors.toList());
                break;
                
            case "company":
                filtered = internships.stream()
                        .filter(i -> i.getCompanyRepresentative() != null && 
                                   i.getCompanyRepresentative().getCompanyName().equalsIgnoreCase(filterValue))
                        .collect(Collectors.toList());
                break;
                
            case "visible":
                boolean isVisible = Boolean.parseBoolean(filterValue);
                filtered = internships.stream()
                        .filter(i -> i.isVisible() == isVisible)
                        .collect(Collectors.toList());
                break;
                
            default:
                System.out.println("Unknown filter type: " + filterType);
                return internships;
        }
        
        return filtered;
    }

    /**
     * Displays a formatted list of internships with detailed information.
     * Shows internship title, company, level, major, status, visibility, period, slots, and applications count.
     *
     * @param internships the list of internships to display
     * @param filterDescription a description of the filter applied (for display purposes)
     */
    public void displayInternships(List<Internship> internships, String filterDescription) {
        if (internships.isEmpty()) {
            System.out.println("No internships found for: " + filterDescription);
            return;
        }
        
        System.out.println("\n=== " + filterDescription + " (" + internships.size() + " found) ===");
        for (int i = 0; i < internships.size(); i++) {
            Internship internship = internships.get(i);
            System.out.println("\n[" + (i + 1) + "] " + internship.getTitle());
            System.out.println("    Company: " + (internship.getCompanyRepresentative() != null 
                    ? internship.getCompanyRepresentative().getCompanyName() 
                    : "N/A"));
            System.out.println("    Level: " + internship.getLevel());
            System.out.println("    Preferred Major: " + internship.getPreferredMajor());
            System.out.println("    Status: " + internship.getStatus());
            System.out.println("    Visible: " + (internship.isVisible() ? "Yes" : "No"));
            System.out.println("    Period: " + internship.getOpeningDate() + " to " + internship.getClosingDate());
            System.out.println("    Slots: " + internship.getSlots());
            System.out.println("    Applications: " + internship.getApplications().size());
        }
    }

    
}

