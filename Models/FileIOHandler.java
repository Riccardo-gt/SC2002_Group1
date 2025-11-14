import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Handles all file I/O operations for the system using CSV format only.
 * Manages persistence for internships, applications, and company representatives.
 */
public class FileIOHandler {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // File paths
    private static final String INTERNSHIPS_FILE = "internships.csv";
    private static final String APPLICATIONS_FILE = "applications.csv";
    private static final String COMPANY_REPS_FILE = "company_reps.csv";

    // ==================== INTERNSHIP PERSISTENCE ====================

    /**
     * Save all internships to CSV file
     * Format: internshipID,title,description,level,preferredMajor,openingDate,closingDate,status,isVisible,companyRepID,slots
     */
    public static void saveInternships(List<Internship> internships, HashMap<String, User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INTERNSHIPS_FILE))) {
            // Write header
            writer.println("internshipID,title,description,level,preferredMajor,openingDate,closingDate,status,isVisible,companyRepID,slots");

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
                        internship.getSlots()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving internships: " + e.getMessage());
        }
    }

    /**
     * Load internships from CSV file
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
                if (parts.length < 11) continue;

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
                            internship.setCompanyRepresentative((CompanyRepresentative) user);
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
     * Save all applications to CSV file
     * Format: applicationID,studentID,internshipID,status,confirmed
     */
    public static void saveApplications(List<Internship> internships) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPLICATIONS_FILE))) {
            // Write header
            writer.println("applicationID,studentID,internshipID,status,confirmed");

            int appID = 1;
            int internshipID = 1;
            for (Internship internship : internships) {
                for (Application application : internship.getApplications()) {
                    writer.printf("%d,%s,%d,%s,%s%n",
                            appID++,
                            application.getStudent().getUserID(),
                            internshipID,
                            application.getStatus().toString(),
                            false // confirmed field from Application class
                    );
                }
                internshipID++;
            }
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }

    /**
     * Load applications from CSV file and link them to internships and students
     */
    public static void loadApplications(List<Internship> internships, HashMap<String, User> users) {
        File file = new File(APPLICATIONS_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                try {
                    String studentID = parts[1];
                    int internshipID = Integer.parseInt(parts[2]) - 1; // 0-indexed
                    String status = parts[3];

                    if (internshipID < 0 || internshipID >= internships.size()) continue;
                    if (!users.containsKey(studentID)) continue;

                    User user = users.get(studentID);
                    if (!(user instanceof Student)) continue;

                    Student student = (Student) user;
                    Internship internship = internships.get(internshipID);

                    Application application = new Application(student, internship);
                    application.setStatus(Application.ApplicationStatus.valueOf(status));

                    internship.addApplication(application);
                    student.applications.add(application);

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
     * Save company representatives to CSV file
     * Format: userID,name,email,password,companyName,position,department,status
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
                        rep.getStatus()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving company representatives: " + e.getMessage());
        }
    }

    /**
     * Load company representatives from CSV file
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
                if (parts.length < 8) continue;

                try {
                    CompanyRepresentative rep = new CompanyRepresentative(
                            parts[0], // userID
                            unescapeCSV(parts[1]), // name
                            parts[2], // email
                            parts[3], // password
                            unescapeCSV(parts[4]), // companyName
                            unescapeCSV(parts[5]), // position
                            unescapeCSV(parts[6]), // department
                            parts[7]  // status
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
     * Escape CSV special characters
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Unescape CSV special characters
     */
    private static String unescapeCSV(String value) {
        if (value == null) return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }

    /**
     * Save all data at once
     */
    public static void saveAllData(List<Internship> internships,
                                   List<CompanyRepresentative> reps,
                                   HashMap<String, User> users) {
        saveInternships(internships, users);
        saveApplications(internships);
        saveCompanyReps(reps);
    }
}