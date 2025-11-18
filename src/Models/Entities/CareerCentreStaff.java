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
    String filterTypeStr = "";
    switch (filterType) {
        case 1:
            filterTypeStr = "status";
            break;
        case 2:
            filterTypeStr = "level";
            break;
        case 3:
            filterTypeStr = "major";
            break;
        default:
            System.out.println("Invalid filter type.");
            return;
    }
    
    // Use the inherited filter method from User class
    List<Internship> filtered = filterInternships(allInternships, filterTypeStr, filter);
    
    // Use the inherited display method
    displayInternships(filtered, "Internships with " + filterTypeStr + " = '" + filter + "'");
}

}
