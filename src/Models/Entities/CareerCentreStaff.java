package Models.Entities;

import java.util.*;
import java.util.stream.Collectors;


public class CareerCentreStaff extends User {
    private String role;
    private String department;

    public CareerCentreStaff(String userId, String name, String email, String password, String role, String department) {
        super(userId, name, email, password);
        this.role = role;
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public String getRole() {
        return role;

    }

    public boolean authorizeAccount(CompanyRepresentative representative) { // Should return boolean
        if (representative == null) {
            System.out.println("Invalid company representative.");
            return false;
        }
        System.out.println("Account authorized for: " + representative.getEmail());
        return true;
    }

    public boolean approveInternshipOpportunity(Internship internship) { // Should return boolean
        if (internship == null) {
            System.out.println("Invalid internship.");
            return false;
        }
        internship.setStatus("Approved");
        internship.toggleVisibility(true);
        System.out.println("Internship approved: " + internship.getTitle());
        return true;
    }

    public boolean approveWithdrawal(Application application) { // Should return boolean
        if (application == null) {
            System.out.println("Invalid application.");
            return false;
        }
        application.setStatus(Application.ApplicationStatus.WITHDRAWN);
        application.setWithdrawalStatus(Application.WithdrawalStatus.APPROVED);
        System.out.println("Withdrawal approved for: " + application.getStudent().getName());
        return true;
    }

    public boolean rejectWithdrawal(Application application) { // Should return boolean
        if (application == null) {
            System.out.println("Invalid application.");
            return false;
        }
        application.setWithdrawalStatus(Application.WithdrawalStatus.REJECTED);
        System.out.println("Withdrawal rejected for: " + application.getStudent().getName());
        return true;
    }
    
public void generateReport(List<Internship> allInternships, String filter, int filterType) {
    System.out.println("=== Career Centre Report ===");
    List<Internship> filtered = allInternships;
    
    switch (filterType) {
        case 1: // Filter by Status
            if (filter != null && !filter.isEmpty()) {
                filtered = allInternships.stream()
                        .filter(i -> i.getStatus().equalsIgnoreCase(filter))
                        .collect(Collectors.toList());
            }
            System.out.println("Total Internships with status = " + filter + " is " + filtered.size());
            break;
        case 2: // Filter by Level
            if (filter != null && !filter.isEmpty()) {
                filtered = allInternships.stream()
                        .filter(i -> i.getLevel().equalsIgnoreCase(filter))
                        .collect(Collectors.toList());
            }
            System.out.println("Total Internships with level = " + filter + " is " + filtered.size());
            break;
        case 3: // Filter by Preferred Major
            if (filter != null && !filter.isEmpty()) {
                filtered = allInternships.stream()
                        .filter(i -> i.getPreferredMajor().equalsIgnoreCase(filter))
                        .collect(Collectors.toList());
            }
            System.out.println("Total Internships with preferred major = " + filter + " is " + filtered.size());
            break;
    }
    
    
    if (filtered.isEmpty()) {
        System.out.println("No internships match the selected filter.");
    } else {
        System.out.println("\n=== Matching Internships ===");
        for (int i = 0; i < filtered.size(); i++) {
            Internship internship = filtered.get(i);
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

}
