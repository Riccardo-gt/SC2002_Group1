package Models.Utility_Classes;

import Models.Entities.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Handles all file input/output operations for the Internship Placement Management System.
 * Manages persistence for internships, applications, and company representatives using CSV format.
 *
 * <p>This class provides centralized file I/O operations including:</p>
 * <ul>
 *   <li>Loading and saving internship data</li>
 *   <li>Loading and saving application data</li>
 *   <li>Loading and saving company representative data</li>
 *   <li>Password management across all user types</li>
 *   <li>CSV file migration for backward compatibility</li>
 * </ul>
 *
 * <p>File formats:</p>
 * <ul>
 *   <li><b>internships.csv:</b> internshipID, title, description, level, preferredMajor, openingDate, closingDate, status, isVisible, companyRepID, slots</li>
 *   <li><b>applications.csv:</b> applicationID, studentID, internshipID, status, withdrawalStatus, confirmed</li>
 *   <li><b>company_reps.csv:</b> userID, name, email, password, companyName, position, department, status</li>
 * </ul>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public class FileIOHandler {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // File paths
    private static final String INTERNSHIPS_FILE = "Datasets/internships.csv";
    private static final String APPLICATIONS_FILE = "Datasets/applications.csv";
    private static final String COMPANY_REPS_FILE = "Datasets/company_reps.csv";

    // ==================== INTERNSHIP PERSISTENCE ====================

    /**
     * Saves all internships to the CSV file.
     *
     * <p>CSV Format: internshipID, title, description, level, preferredMajor,
     * openingDate, closingDate, status, isVisible, companyRepID, slots</p>
     *
     * @param internships list of internships to save
     * @param users map of users for resolving company representative IDs
     */
    public static void saveInternships(List<Internship> internships, HashMap<String, User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INTERNSHIPS_FILE))) {
            // Write header
            writer.println(
                    "internshipID,title,description,level,preferredMajor,openingDate,closingDate,status,isVisible,companyRepID,slots");

            int id = 1;
            for (Internship internship : internships) {
                CompanyRepresentative rep = internship.getCompanyRepresentative();
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d%n",
                        id++,
                        escapeCSV(internship.getTitle()),
                        escapeCSV(internship.getDescription()),
                        internship.getLevel(),
                        internship.getPreferredMajor(),
                        internship.getOpeningDate() != null ? internship.getOpeningDate().format(DATE_FORMATTER) : "",
                        internship.getClosingDate() != null ? internship.getClosingDate().format(DATE_FORMATTER) : "",
                        internship.getStatus(),
                        internship.isVisible(),
                        rep != null ? rep.getUserID() : "",
                        internship.getSlots());
            }
        } catch (IOException e) {
            System.err.println("Error saving internships: " + e.getMessage());
        }
    }

    /**
     * Loads internships from the CSV file.
     * Links each internship to its company representative if available.
     *
     * @param users map of users for resolving company representative IDs
     * @return list of loaded internships
     */
    public static List<Internship> loadInternships(HashMap<String, User> users) {
        List<Internship> internships = new ArrayList<>();
        File file = new File(INTERNSHIPS_FILE);

        if (!file.exists()) {
            return internships;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                if (parts.length < 11)
                    continue;

                try {
                    Internship internship = new Internship();
                    internship.setTitle(unescapeCSV(parts[1]));
                    internship.setDescription(unescapeCSV(parts[2]));
                    internship.setLevel(parts[3]);
                    internship.setPreferredMajor(parts[4]);

                    if (!parts[5].isEmpty()) {
                        internship.setOpeningDate(LocalDate.parse(parts[5], DATE_FORMATTER));
                    }
                    if (!parts[6].isEmpty()) {
                        internship.setClosingDate(LocalDate.parse(parts[6], DATE_FORMATTER));
                    }

                    internship.setStatus(parts[7]);
                    internship.toggleVisibility(Boolean.parseBoolean(parts[8]));

                    String repID = parts[9];
                    if (!repID.isEmpty() && users.containsKey(repID)) {
                        User user = users.get(repID);
                        if (user instanceof CompanyRepresentative) {
                            CompanyRepresentative rep = (CompanyRepresentative) user;
                            rep.loadInternships(internship);
                            internship.setCompanyRepresentative(rep);
                        }
                    }

                    internship.setSlots(Integer.parseInt(parts[10]));
                    internships.add(internship);
                } catch (Exception e) {
                    System.err.println("Error parsing internship line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading internships: " + e.getMessage());
        }

        return internships;
    }

    // ==================== APPLICATION PERSISTENCE ====================

    /**
     * Saves all applications to the CSV file.
     *
     * <p>CSV Format: applicationID, studentID, internshipID, status, withdrawalStatus, confirmed</p>
     *
     * @param internships list of internships containing applications to save
     */
    public static void saveApplications(List<Internship> internships) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPLICATIONS_FILE))) {
            // Write header
            writer.println("applicationID,studentID,internshipID,status,withdrawalStatus,confirmed");

            int appID = 1;
            int internshipID = 1;
            for (Internship internship : internships) {
                for (Application application : internship.getApplications()) {
                    writer.printf("%d,%s,%d,%s,%s,%b%n",
                            appID++,
                            application.getStudent().getUserID(),
                            internshipID,
                            application.getStatus().toString(),
                            application.getWithdrawalStatus().toString(),
                            application.isConfirmed());
                }
                internshipID++;
            }
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }

    /**
     * Loads applications from the CSV file and links them to internships and students.
     *
     * @param internships list of internships to link applications to
     * @param users map of users for resolving student IDs
     */
    public static void loadApplications(List<Internship> internships, HashMap<String, User> users) {
        File file = new File(APPLICATIONS_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 4)
                    continue;

                try {
                    String studentID = parts[1];
                    int internshipID = Integer.parseInt(parts[2]) - 1; // 0-indexed
                    String status = parts[3];
                    String withdrawalStr = parts.length >= 5 ? parts[4] : "";

                    if (internshipID < 0 || internshipID >= internships.size())
                        continue;
                    if (!users.containsKey(studentID))
                        continue;

                    User user = users.get(studentID);
                    if (!(user instanceof Student))
                        continue;

                    Student student = (Student) user;
                    Internship internship = internships.get(internshipID);

                    Application application = new Application(student, internship);
                    application.setStatus(Application.ApplicationStatus.valueOf(status));

                    internship.addApplication(application);
                    student.getApplications().add(application);

                } catch (Exception e) {
                    System.err.println("Error parsing application line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading applications: " + e.getMessage());
        }
    }

    // ==================== COMPANY REPRESENTATIVE PERSISTENCE ====================

    /**
     * Saves company representatives to the CSV file.
     *
     * <p>CSV Format: userID, name, email, password, companyName, position, department, status</p>
     *
     * @param reps list of company representatives to save
     */
    public static void saveCompanyReps(List<CompanyRepresentative> reps) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPANY_REPS_FILE))) {
            // Write header
            writer.println("userID,name,email,password,companyName,position,department,status");

            for (CompanyRepresentative rep : reps) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                        rep.getUserID(),
                        escapeCSV(rep.getName()),
                        rep.getEmail(),
                        rep.getPassword(),
                        escapeCSV(rep.getCompanyName()),
                        escapeCSV(rep.getPosition()),
                        escapeCSV(rep.getDepartment()),
                        rep.getStatus());
            }
        } catch (IOException e) {
            System.err.println("Error saving company representatives: " + e.getMessage());
        }
    }

    /**
     * Loads company representatives from the CSV file.
     *
     * @return list of loaded company representatives
     */
    public static List<CompanyRepresentative> loadCompanyReps() {
        List<CompanyRepresentative> reps = new ArrayList<>();
        File file = new File(COMPANY_REPS_FILE);

        if (!file.exists()) {
            return reps;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 8)
                    continue;

                try {
                    CompanyRepresentative rep = new CompanyRepresentative(
                            parts[0], // userID
                            unescapeCSV(parts[1]), // name
                            parts[2], // email
                            parts[3], // password
                            unescapeCSV(parts[4]), // companyName
                            unescapeCSV(parts[5]), // position
                            unescapeCSV(parts[6]), // department
                            parts[7] // status
                    );
                    reps.add(rep);
                } catch (Exception e) {
                    System.err.println("Error parsing company rep line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading company representatives: " + e.getMessage());
        }

        return reps;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Escapes CSV special characters in a string.
     * Wraps the value in quotes if it contains commas, quotes, or newlines.
     * Doubles any existing quotes as per CSV standard.
     *
     * @param value the string to escape
     * @return the escaped string
     */
    private static String escapeCSV(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Unescapes CSV special characters from a string.
     * Removes surrounding quotes and converts doubled quotes back to single quotes.
     *
     * @param value the string to unescape
     * @return the unescaped string
     */
    private static String unescapeCSV(String value) {
        if (value == null)
            return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }

    /**
     * Saves all data (internships, applications, and company representatives) at once.
     * This is a convenience method for batch saving operations.
     *
     * @param internships list of internships to save
     * @param reps list of company representatives to save
     * @param users map of all users
     */
    public static void saveAllData(List<Internship> internships,
            List<CompanyRepresentative> reps,
            HashMap<String, User> users) {
        saveInternships(internships, users);
        saveApplications(internships);
        saveCompanyReps(reps);
    }

    // ==================== PASSWORD HANDLING METHODS ====================

    /**
     * Updates password for a specific user in their respective CSV file.
     * Delegates to the appropriate password update method based on user type.
     *
     * @param user the user whose password should be updated
     * @return true if the password was successfully updated, false otherwise
     */
    public static boolean updateUserPassword(User user) {
        if (user instanceof Student) {
            return updateStudentPassword((Student) user);
        } else if (user instanceof CompanyRepresentative) {
            return updateCompanyRepPassword((CompanyRepresentative) user);
        } else if (user instanceof CareerCentreStaff) {
            return updateStaffPassword((CareerCentreStaff) user);
        }
        return false;
    }

    /**
     * Updates password for a student in the student_list.csv file.
     *
     * @param student the student whose password should be updated
     * @return true if successful, false otherwise
     */
    private static boolean updateStudentPassword(Student student) {
        return updatePasswordInFile("Datasets/student_list.csv", student.getUserID(), student.getPassword(), 0, 5);
    }

    /**
     * Updates password for a company representative in the company_reps.csv file.
     *
     * @param rep the company representative whose password should be updated
     * @return true if successful, false otherwise
     */
    private static boolean updateCompanyRepPassword(CompanyRepresentative rep) {
        return updatePasswordInFile("Datasets/company_reps.csv", rep.getUserID(), rep.getPassword(), 0, 3);
    }

    /**
     * Updates password for a staff member in the staff_list.csv file.
     *
     * @param staff the staff member whose password should be updated
     * @return true if successful, false otherwise
     */
    private static boolean updateStaffPassword(CareerCentreStaff staff) {
        return updatePasswordInFile("Datasets/staff_list.csv", staff.getUserID(), staff.getPassword(), 0, 5);
    }

    /**
     * Generic method to update password in any CSV file.
     * Reads the entire file, updates the matching user's password, and writes back.
     *
     * @param filePath path to the CSV file
     * @param userID ID of the user to update
     * @param newPassword new password to set
     * @param idColumnIndex column index where userID is located (0-based)
     * @param passwordColumnIndex column index where password is located (0-based)
     * @return true if successful, false otherwise
     */
    private static boolean updatePasswordInFile(String filePath, String userID, String newPassword,
            int idColumnIndex, int passwordColumnIndex) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            return false;
        }

        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Read header
            if (line != null) {
                lines.add(line);
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length > Math.max(idColumnIndex, passwordColumnIndex) &&
                        parts[idColumnIndex].trim().equals(userID)) {
                    // Update the password column
                    parts[passwordColumnIndex] = newPassword;
                    lines.add(String.join(",", parts));
                    updated = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        }

        if (!updated) {
            System.err.println("User not found: " + userID);
            return false;
        }

        // Write updated content back to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.println(line);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Migration method: Adds a default password column to the student_list.csv if
     * missing.
     */
    public static void StudentCSVIncludePasswords() {
        migratePasswordColumn("Datasets/student_list.csv");
    }

    /**
     * Migration method: Adds a default password column to the staff_list.csv if
     * missing.
     */
    public static void StaffCSVIncludePasswords() {
        migratePasswordColumn("Datasets/staff_list.csv");
    }

    /**
     * Migration method: Adds a default password column to the company_reps.csv if
     * missing.
     */
    public static void CompanyRepCSVIncludePasswords() {
        // You can use the INTERNSHIPS_FILE constant if you defined it earlier,
        // but using the direct path here keeps it consistent with the others.
        migratePasswordColumn("Datasets/company_reps.csv");
    }

    /**
     * Generic utility to migrate any user CSV file to include a default password column.
     * Checks if the password column already exists and adds it with default value "password" if missing.
     *
     * @param filePath path to the CSV file to migrate
     */
    private static void migratePasswordColumn(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Migration failed: File not found at " + filePath);
                return;
            }

            List<String> lines = new ArrayList<>();
            // Using try-with-resources for automatic resource closing
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String header = reader.readLine();

                // Check if password column already exists (case-insensitive check for
                // "password")
                if (header != null && header.toLowerCase().contains("password")) {
                    return; // Migration is not needed
                }

                // 1. Add Password column to header
                lines.add(header + ",Password");

                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        // 2. Add default password to each user record
                        lines.add(line + ",password");
                    }
                }
            } // reader automatically closed here

            // Write updated content back to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String l : lines) {
                    writer.println(l);
                }
            } // writer automatically closed here

        } catch (IOException e) {
            System.err.println("Error migrating CSV for " + filePath + ": " + e.getMessage());
        }
    }
}