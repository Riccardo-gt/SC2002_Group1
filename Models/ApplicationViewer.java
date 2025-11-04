import java.util.*;
import java.util.stream.Collectors;

public class ApplicationViewer {
    private Internship currentInternship;
    private List<Application> applications;

    public ApplicationViewer(Internship internship) {
        this.currentInternship = internship;
        this.applications = new ArrayList<>(internship.getApplications());
    }

    // Display all applicants for the current internship
    public void displayApplicants() {
        System.out.println("=== Applicants for: " + currentInternship.getTitle() + " ===");
        System.out.println("Total Applications: " + applications.size());
        
        if (applications.isEmpty()) {
            System.out.println("No applications received yet.");
            return;
        }

        int counter = 1;
        for (Application application : applications) {
            Student student = application.getStudent();
            System.out.println(counter + ". " + student.getName() + 
                             " | Status: " + application.getStatus());
            counter++;
        }
    }

    // Rank applicants based on criteria (GPA, major match, and year of study)
    public void rankApplicants() {
        System.out.println("=== Ranked Applicants for: " + currentInternship.getTitle() + " ===");
        
        List<Application> rankedApplications = applications.stream()
                .sorted((a1, a2) -> Integer.compare(
                        calculateApplicantScore(a2), calculateApplicantScore(a1)))
                .collect(Collectors.toList());

        int rank = 1;
        for (Application app : rankedApplications) {
            Student student = app.getStudent();
            int score = calculateApplicantScore(app);
            System.out.println(rank + ". " + student.getName() +
                               " | Score: " + score +
                               " | Status: " + app.getStatus());
            rank++;
        }
    }

    private int calculateApplicantScore(Application application) {
        Student s = application.getStudent();
        int score = 0;

        // Higher GPA = better score
        score += (int)(s.getCGPA() * 10);

        // Match preferred major
        if (s.getMajor().equalsIgnoreCase(currentInternship.getPreferredMajor())) {
            score += 20;
        }

        // Higher study year = more experience
        score += s.getYearOfStudy() * 5;

        return score;
    }

    // Approve an application
    public boolean approveApplication(Application application) {
        if (!applications.contains(application)) {
            System.out.println("Application not found for this internship.");
            return false;
        }

        if (application.getStatus() == Application.ApplicationStatus.APPROVED) {
            System.out.println("Application is already approved.");
            return true;
        }

        application.setStatus(Application.ApplicationStatus.APPROVED);
        System.out.println("Application approved for: " + application.getStudent().getName());
        return true;
    }

    // Reject an application
    public boolean rejectApplication(Application application) {
        if (!applications.contains(application)) {
            System.out.println("Application not found for this internship.");
            return false;
        }

        if (application.getStatus() == Application.ApplicationStatus.REJECTED) {
            System.out.println("Application is already rejected.");
            return true;
        }

        application.setStatus(Application.ApplicationStatus.REJECTED);
        System.out.println("Application rejected for: " + application.getStudent().getName());
        return true;
    }
}