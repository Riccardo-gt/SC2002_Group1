package Models.Entities;

import Models.Utility_Classes.InternshipViewer;

import java.util.*;

public class Student extends User {
    String major = "";
    int studyYear = 0;
    float cgpa;
    List<Application> applications;
    private boolean hasAcceptedPlacement = false;

    public Student(String userId, String name, String email, String password, String major, int studyYear, float cgpa) {
        super(userId, name, email, password);
        this.major = major;
        this.studyYear = studyYear;
        this.cgpa = cgpa;
        applications = new ArrayList<Application>();
    }

    public String getMajor() {
        return this.major;
    }

    public String getName() {
        return super.getName();
    }

    public Float getCGPA() {
        return this.cgpa;
    }

    public int getYearOfStudy() {
        return this.studyYear;
    }

    public List<Application> getApplications() {
        return applications;
    }

    /**
     * Check if student can apply for more internships (max 3)
     */
    public boolean canApply() {
        long activeApplications = applications.stream()
                .filter(app -> app.getStatus() != Application.ApplicationStatus.UNSUCCESSFUL)
                .count();
        return activeApplications < 3;
    }

    /**
     * Check if student is eligible for a given internship level
     * Year 1-2: Basic only
     * Year 3+: All levels
     */
    public boolean isEligibleForLevel(String level) {
        if (studyYear <= 2) {
            return "Basic".equalsIgnoreCase(level);
        }
        return true; // Year 3+ can apply to all levels
    }

    /**
     * Check if internship matches student's major
     */
    public boolean matchesMajor(String preferredMajor) {
        return this.major.equalsIgnoreCase(preferredMajor);
    }

    public void viewInternshipOpportunities(InternshipViewer internshipViewer) {
        internshipViewer.viewInternships();
    }

    public boolean applyForInternship(Internship internship) {
        // Validation checks
        if (!canApply()) {
            System.out.println("Error: Maximum of 3 active applications allowed.");
            return false;
        }

        if (!isEligibleForLevel(internship.getLevel())) {
            System.out.println("Error: You are not eligible for " + internship.getLevel() + " level internships.");
            return false;
        }

        if (!internship.isAcceptingApplications()) {
            System.out.println("Error: This internship is not accepting applications.");
            return false;
        }

        // Check for duplicate application
        boolean alreadyApplied = applications.stream()
                .anyMatch(app -> app.getInternship().equals(internship));
        if (alreadyApplied) {
            System.out.println("Error: You have already applied for this internship.");
            return false;
        }

        // Create and submit application
        Application application = new Application(this, internship);
        internship.addApplication(application);
        applications.add(application);
        System.out.println("Application submitted for: " + internship.getTitle());
        return true;
    }

    public void viewAppliedInternships() {
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }

        System.out.println("\n=== Your Applications ===");
        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            Internship internship = app.getInternship();
            System.out.println((i + 1) + ". " + internship.getTitle() +
                    " | Status: " + app.getStatus() +
                    " | Placement Status: " + app.isConfirmed() +
                    " | Withdrawal Status: " + app.getWithdrawalStatus() +
                    " | Company: " + (internship.getCompanyRepresentative() != null ?
                    internship.getCompanyRepresentative().getCompanyName() : "N/A"));
        }
    }

    public boolean acceptPlacement(Application application) {
        if (!applications.contains(application)) {
            System.out.println("Error: Application not found.");
            return false;
        }

        if (application.getStatus() != Application.ApplicationStatus.SUCCESSFUL) {
            System.out.println("Error: Can only accept approved applications.");
            return false;
        }

        // Check if student has already accepted a placement
        if (hasAcceptedPlacement) {
            System.out.println("Error: You have already accepted a placement. You can only accept one internship.");
            return false;
        }

        // Confirm acceptance
        application.confirmAcceptance();
        hasAcceptedPlacement = true;

        Internship internship = application.getInternship();
        System.out.println("You have accepted the placement for: " + internship.getTitle());

        // Withdraw all other applications (both PENDING and SUCCESSFUL)
        for (Application app : applications) {
            if (app != application && (app.getStatus() == Application.ApplicationStatus.PENDING ||
                                       app.getStatus() == Application.ApplicationStatus.SUCCESSFUL)) {
                app.setStatus(Application.ApplicationStatus.WITHDRAWN);
                System.out.println("Automatically withdrew application for: " + app.getInternship().getTitle());
            }
        }

        return true;
    }

    public boolean requestWithdrawal(Application application) {
        if (!applications.contains(application)) {
            System.out.println("Error: Application not found.");
            return false;
        }
        if (application.getWithdrawalStatus() != Application.WithdrawalStatus.NOTREQUESTED) {
            System.out.println("Error: Withdrawal already requested.");
            return false;
        }
        application.setWithdrawalStatus(Application.WithdrawalStatus.PENDING);
        System.out.println("Withdrawal requested for: " + application.getInternship().getTitle());
        System.out.println("Please contact Career Centre Staff for approval.");
        return true;
    }
}