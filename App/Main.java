import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
    
public class Main {
    static HashMap<String, User> registeredAccounts = new HashMap<String, User>();
    public static void main(String[] args) {
        registerStudents();
        registerStaff();
        System.out.println("Welcome to the Internship Placement Management System");
        /* User user = login(); // Call login function from Login.java
        if (user instanceof Student) {
            Student student = (Student) user;
        }
        */

    }

    static void registerStudents() {
        try {
            BufferedReader file = new BufferedReader(new FileReader("students.txt")); // Read line by line
            String line = file.readLine(); // Read the header

            while ((line = file.readLine()) != null) { // Assign next line to line variable and check if != null simaltaneously
                //System.out.println(line);
                String[] parts = line.split("\t"); // parts -> [userID, name, course, year, email]
                //System.out.println(Arrays.toString(parts));
                Student student = new Student(parts[0], parts[1], parts[4], "1234", parts[2], Integer.valueOf(parts[3]));
                registeredAccounts.put(parts[0], student);
            }
            file.close();
        } 
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    static void registerStaff() {
        try {
            BufferedReader file = new BufferedReader(new FileReader("staff.txt")); 
            String line = file.readLine(); 

            while ((line = file.readLine()) != null) { 
                //System.out.println(line);
                String[] parts = line.split(","); // parts -> [userID, name, role, department, email]
                //System.out.println(Arrays.toString(parts));
                CareerCentreStaff staff = new CareerCentreStaff(parts[0], parts[1], parts[4], "1234", parts[2], parts[3]);
                registeredAccounts.put(parts[0], staff);
            }
            file.close();
        } 
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    
}

