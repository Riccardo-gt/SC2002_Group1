
import java.util.ArrayList;
import java.util.List;

public class CompanyRepresentative extends User {
    private String companyName;
    private String position;
    private String department;
    private String status;
    private List<Internship> createdInternships;
    private List<CompanyRepresentative> allCompanyReps;

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

    public CompanyRepresentative registerCompanyRep(String userId, String email, String password, String companyName, String department, String position) {
        // input validation
        if (email.isEmpty() || companyName.isEmpty() || department.isEmpty() || position.isEmpty()) {
            System.out.println("Please fill in all fields.");
        }
        if (!email.contains("@")) {
            System.out.println("Invalid email format.");
        }
        
        CompanyRepresentative newRep = new CompanyRepresentative(userId, name, email, password, companyName, position, department, "PENDING");
        return newRep;
    }

    public void createInternshipOpportunity(Internship internship) {
        createdInternships.add(internship);
        System.out.println("Internship opportunity created: " + internship.getTitle());
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
