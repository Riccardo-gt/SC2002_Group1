package Models.Utility_Classes;

import Models.Entities.Application;
import Models.Entities.Internship;
import Models.Entities.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for viewing and managing internship listings.
 * Provides functionality to display internships, filter by availability,
 * submit applications, and request withdrawals.
 *
 * <p>This class acts as a facade for internship-related operations,
 * simplifying the interaction between students and internship opportunities.</p>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public class InternshipViewer {
    private List<Internship> internships;

    /**
     * Constructs a new InternshipViewer with an empty internship list.
     */
    public InternshipViewer() {
        internships = new ArrayList<Internship>();
    }

    public List<Internship> getInternships() {
        return internships;
    }

    public void addInternship(Internship internship) {
        internships.add(internship);
    }

    /**
     * Displays all internships in the viewer's list.
     * Shows detailed information for each internship.
     */
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
        Internship internship = application.getInternship();
        // Mark as withdrawn (could use REJECTED or create WITHDRAWN status)
        application.setWithdrawalStatus(Application.WithdrawalStatus.PENDING);
        System.out.println("Withdrawal requested for application to: " + internship.getTitle());
        System.out.println("Please contact Career Centre Staff for approval.");
    }

    /**
     * Gets a list of internships that are currently accepting applications.
     *
     * @return list of open internships
     */
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
