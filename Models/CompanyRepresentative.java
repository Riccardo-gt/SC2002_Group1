package Models;

import java.util.ArrayList;
import java.util.List;

public class CompanyRepresentative extends User {
    private String companyName;
    private String position;
    private String department;
    private String status;
    private List<Internship> createdInternships;
    private static List<CompanyRepresentative> allCompanyReps = new ArrayList<>();

    public CompanyRepresentative(String userId, String name, String email, String password, String companyName, String position, String department, String status) {
        super(userId, name, email, password);
        this.companyName = companyName;
        this.position = position;
        this.department = department;
        this.status = status;
        this.createdInternships = new ArrayList<>();
        allCompanyReps.add(this);
    }

    public List<CompanyRepresentative> getAllCompanyReps() {
        return allCompanyReps;
    }

    public List<Internship> getCreatedInternships() {
        return createdInternships;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPosition() {
        return position;
    }

    public String getDepartment() {
        return department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean canCreateInternship() {
        return createdInternships.size() < 5;
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public CompanyRepresentative registerCompanyRep(String userId, String name, String email, String password, String companyName, String department, String position) {
        // input validation
        if (email.isEmpty() || companyName.isEmpty() || department.isEmpty() || position.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return null;
        }
        if (!email.contains("@")) {
            System.out.println("Invalid email format.");
            return null;
        }

        return new CompanyRepresentative(userId, name, email, password, companyName, position, department, "PENDING");
    }

    public void createInternshipOpportunity(Internship internship) {
        if (!canCreateInternship()) {
            System.out.println("Error: Maximum of 5 internships allowed per representative.");
            return;
        }
        createdInternships.add(internship);
        System.out.println("Internship opportunity created: " + internship.getTitle());
    }

    public boolean deleteInternship(Internship internship) {
        if (!createdInternships.contains(internship)) {
            System.out.println("You do not manage this internship.");
            return false;
        }
        if ("APPROVED".equals(internship.getStatus())) {
            System.out.println("Cannot delete approved internships.");
            return false;
        }
        createdInternships.remove(internship);
        System.out.println("Internship deleted: " + internship.getTitle());
        return true;
    }

    public boolean editInternship(Internship internship) {
        if (!createdInternships.contains(internship)) {
            System.out.println("You do not manage this internship.");
            return false;
        }
        if ("APPROVED".equals(internship.getStatus())) {
            System.out.println("Cannot edit approved internships.");
            return false;
        }
        return true;
    }

    public void viewApplications() {
        for (Internship internship : createdInternships) {
            ApplicationViewer viewer = new ApplicationViewer(internship);
            viewer.displayApplicants();
        }
    }

    public void changeVisibility(Internship internship, boolean visible) {
        if (!createdInternships.contains(internship)) {
            System.out.println("You do not manage this internship.");
            return;
        }
        internship.toggleVisibility(visible);
        System.out.println("Visibility for " + internship.getTitle() + " set to " + visible);
    }

    public void manageApplication(Internship internship, Application application, boolean approve) {
        ApplicationViewer viewer = new ApplicationViewer(internship);
        if (approve) {
            viewer.approveApplication(application);
        } else {
            viewer.rejectApplication(application);
        }
    }

}