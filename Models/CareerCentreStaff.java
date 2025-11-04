import java.util.*;

public class CareerCentreStaff extends User {
    private String role;
    private String department;

    public CareerCentreStaff(String userId, String name, String email, String password, String role,
            String department) {
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
        System.out.println("Account authorized for: " + representative.email);
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
        application.setStatus(Application.ApplicationStatus.REJECTED);
        System.out.println("Withdrawal approved for: " + application.getStudent().getName());
        return true;
    }

    public void generateReport(List<Internship> allInternships) {
        System.out.println("=== Career Centre Report ===");

        int totalInternships = allInternships.size();
        int approvedCount = 0;
        int pendingCount = 0;
        int rejectedCount = 0;
        int filledCount = 0;

        for (Internship internship : allInternships) {
            if (internship.status == null) {
                pendingCount++;
            } else if (internship.status.equals("Approved")) {
                approvedCount++;
            } else if (internship.status.equals("Pending")) {
                pendingCount++;
            } else if (internship.status.equals("Rejected")) {
                rejectedCount++;
            } else if (internship.status.equals("Filled")) {
                filledCount++;
            }
        }

        System.out.println("Total Internships: " + totalInternships);
        System.out.println("Approved: " + approvedCount);
        System.out.println("Pending: " + pendingCount);
        System.out.println("Rejected: " + rejectedCount);
        System.out.println("Filled: " + filledCount);

        int totalApplications = 0;
        for (Internship internship : allInternships) {
            if (internship.getApplications() != null) {
                totalApplications += internship.getApplications().size();
            }
        }
        System.out.println("Total Applications: " + totalApplications);
    }

}
