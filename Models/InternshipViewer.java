import java.util.ArrayList;
import java.util.List;

public class InternshipViewer {
    private List<Internship> internships;
    
    public InternshipViewer() {
        internships = new ArrayList<Internship>();
    }

    public List<Internship> getInternships() {
        return internships;
    }

    public void addInternship(Internship internship) {
        internships.add(internship);
    }

    public void viewInternships() {
        for (Internship internship: internships) {
            internship.displayDetails();
            System.out.println("---");
        }
    }
    
    public void applyForInternship(Application application, Internship internship) {
        if (internship.isAcceptingApplications()) {
            internship.addApplication(application);
            System.out.println("Application submitted for: " + internship.getTitle());
        } else {
            System.out.println("Cannot apply - internship is not accepting applications");
        }
    }

    public void requestWithdrawal(Student student, Application application) {
        application.setStatus(Application.ApplicationStatus.REJECTED);
        System.out.println("Withdrawal requested for application");
    }

    public List<Internship> getOpenInternships() {
        List<Internship> openInternships = new ArrayList<>();
        for (Internship internship : internships) {
            if (internship.isAcceptingApplications()) {
                openInternships.add(internship);
            }
        }
        return openInternships;
    }

    public void displayOpenInternships() {
        System.out.println("=== OPEN INTERNSHIPS ===");
        for (Internship internship : getOpenInternships()) {
            internship.displayDetails();
            System.out.println("---");
        }
    }
}
