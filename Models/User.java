import java.util.*;
    
public class User {
    String userID = "", name = "", email = "", password = "";

    public User(String userId, String name, String email, String password) {
        this.userID = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    void changePassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter new password (Ensure it has atleast 1 Uppercase letter, 1 Lowercase letter, 1 special character (!@#$%) and 1 number)");
        String tempPassword = scanner.next();
        while (isPasswordValid(tempPassword) == false) {
            System.out.println("Please re-enter password");
            tempPassword = scanner.next();
        }
        this.password = tempPassword;
        scanner.close();
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

    void login() {

    }

    void logout() {

    }

    void filterBy(String filter) {

    }
}


