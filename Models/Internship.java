package Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Internship {
    private String title;
    private String description;
    private String level; // Basic, Intermediate, Advanced
    private String preferredMajor;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String status; // Pending, Approved, Rejected, Filled
    private boolean isVisible;
    private CompanyRepresentative representative;
    private int slots;
    private List<Application> applications;

    public Internship() {
        this.applications = new ArrayList<>();
        this.isVisible = true;
        this.status = "Pending";
        this.slots = 1;
    }

    public Internship(String title, String description, String level, 
                     String preferredMajor, LocalDate openingDate, 
                     LocalDate closingDate, int slots, CompanyRepresentative representative) {
        this();
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.slots = slots;
        this.representative = representative;
    }

    // Getters
    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return description;
    }

    public String getLevel() {
        return level;
    }

    public String getPreferredMajor() {
        return this.preferredMajor;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public String getStatus() {
        return status;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public CompanyRepresentative getCompanyRepresentative() {
        return this.representative;
    }

    public int getSlots() {
        return slots;
    }

    public List<Application> getApplications() {
        return new ArrayList<>(applications);
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setPreferredMajor(String preferredMajor) {
        this.preferredMajor = preferredMajor;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCompanyRepresentative(CompanyRepresentative representative) {
        this.representative = representative;
    }

    public void setSlots(int slots) {
        this.slots = slots;
        checkFilledStatus();
    }

    
    public void printApplications() {
        if (applications.isEmpty()) {
            System.out.println("No applications found for internship: " + title);
            return;
        }
        
        System.out.println("Applications for Internship: " + title);
        System.out.println("=========================================");
        for (Application application : applications) {
            Student student = application.getStudent();
            System.out.println("Student: " + student.getName() + 
                             " | Major: " + student.getMajor() +
                             " | Status: " + application.getStatus());
        }
    }

    public void addApplication(Application application) {
        if (application != null && !applications.contains(application)) {
            applications.add(application);
            checkFilledStatus();
        }
    }

    public boolean isAcceptingApplications() {
        LocalDate today = LocalDate.now();
        return isVisible && "Approved".equals(status) && !today.isBefore(openingDate) && !today.isAfter(closingDate);
    }

    public void toggleVisibility(boolean isVisible) {
        this.isVisible = isVisible;
        System.out.println("Internship visibility set to: " + isVisible);
    }

    public void checkFilledStatus() {
        if (slots > 0) {
            long acceptedCount = applications.stream()
                .filter(app -> app.getStatus() == Application.ApplicationStatus.SUCCESSFUL)
                .count();
            
            if (acceptedCount >= slots && !"Filled".equals(status)) {
                this.status = "Filled";
                System.out.println("Internship '" + title + "' is now filled.");
            } else if (acceptedCount < slots && "Filled".equals(status)) {
                this.status = "Approved";
            }
        }
    }

    public void displayDetails() {
        System.out.println("=== Internship Details ===");
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Level: " + level);
        System.out.println("Preferred Major: " + preferredMajor);
        System.out.println("Period: " + openingDate + " to " + closingDate);
        System.out.println("Status: " + status);
        System.out.println("Visible: " + (isVisible ? "Yes" : "No"));
        System.out.println("Slots: " + slots);
        System.out.println("Applications Received: " + applications.size());
        if (representative != null) {
            System.out.println("Company Representative: " + representative.getName());
        }
    }
}

