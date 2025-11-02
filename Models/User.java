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

    void filterBy() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number for the filter you want to apply:\n"
                   + "1: Status\n"
                   + "2: Preferred Majors\n"
                   + "3: Internship Level\n"
                   + "4: Closing Date");
        
        int filter = scanner.nextInt();
        switch (filter) {
            case 1: // Do in while loop
                System.out.println("Enter the status of the internship:\n"
                   + "1: Pending\n"
                   + "2: Approved\n"
                   + "3: Rejected\n"
                   + "4: Filled");
                String status = scanner.next();
                // Do not need switch case, can just loop through internships and print those == status
                switch (status) {
                    case "Pending":
                        // ...
                        break;
                    case "Approved":
                        // ...
                        break;
                    case "Rejected":
                        // ...
                        break;
                    case "Filled":
                        // ...
                        break;
                    default:
                        System.out.println("Invalid status! Please try again"); // Prompt again
                }
                break;
            case 2:
                System.out.println("Please input your preferred major");
                String major = scanner.nextLine();
                // ...
                break;
            case 3:
                System.out.println("Please input your preferred internship level");
                String level = scanner.nextLine();
                // ...
                break;
            case 4:
                System.out.println("Please input your closing date");
                // Use date object
                // ...
                break;
            default:
                System.out.println("Invalid filter!"); // Prompt again
        }

        scanner.close();
            
    }
}


