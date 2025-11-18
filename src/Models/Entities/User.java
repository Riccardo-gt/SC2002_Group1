package Models.Entities;

import Models.Utility_Classes.FileIOHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

// User is an abstract base class as all users (Student, CareerCentreStaff) are one of the specific roles.
public abstract class User {
    // Encapsulation: All attributes are private 
    private String userID;
    private String name;
    private String email;
    private String password;

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
        
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword); // Use hashing
    }

    public void logout() {
        System.out.println(this.name + " (" + this.userID + ") has been logged out.");
    }

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
     * Display filtered internships in a consistent format
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

