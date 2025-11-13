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
    // Default password as per assignment: "password" [cite: 37]
    private static final String DEFAULT_PASSWORD = "password"; 

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
        
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword); // Use hashing
    }

    public void logout() {
        System.out.println(this.name + " (" + this.userID + ") has been logged out.");
    }

    public void changePassword(Scanner scanner) {
        System.out.println("--- Change Password ---");
        String currentPassword;
        String newPassword;

        // 1. Verify Current Password
        System.out.print("Enter current password: ");
        currentPassword = scanner.nextLine(); // Using nextLine() for password input

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
        System.out.println("Password successfully updated. Please log in again with your new password.");
    }
    
    private boolean isPasswordValid(String password) {
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

    
}

