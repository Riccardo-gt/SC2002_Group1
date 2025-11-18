package Models.Entities;

import Models.Utility_Classes.InternshipViewer;

import java.util.*;


/**
 * Represents a student user in the Internship Placement Management System.
 * Students can browse internships, submit applications, accept placements, and request withdrawals.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Can apply for up to 3 internships simultaneously</li>
 *   <li>Year 1-2 students can only apply for Basic level internships</li>
 *   <li>Year 3+ students can apply for all levels</li>
 *   <li>Can accept only one placement offer</li>
 *   <li>Must match the preferred major of the internship</li>
 * </ul>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public class Student extends User {
    String major = "";
    int studyYear = 0;
    float cgpa;
    List<Application> applications;
    private boolean hasAcceptedPlacement = false;

    /**
     * Constructs a new Student with the specified details.
     *
     * @param userId unique identifier for the student
     * @param name full name of the student
     * @param email email address of the student
     * @param password password for authentication
     * @param major the student's major/field of study
     * @param studyYear the student's current year of study
     * @param cgpa the student's Cumulative Grade Point Average
     */
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
     * Checks if the student can apply for more internships.
     * Students are limited to a maximum of 3 active applications
     * (applications that are not marked as UNSUCCESSFUL).
     *
     * @return true if the student can apply for more internships, false otherwise
     */
    public boolean canApply() {
        long activeApplications = applications.stream()
                .filter(app -> app.getStatus() != Application.ApplicationStatus.UNSUCCESSFUL)
                .count();
        return activeApplications < 3;
    }

    /**
     * Checks if the student is eligible for a given internship level based on year of study.
     * <ul>
     *   <li>Year 1-2: Basic level only</li>
     *   <li>Year 3+: All levels (Basic, Intermediate, Advanced)</li>
     * </ul>
     *
     * @param level the internship level to check eligibility for
     * @return true if the student is eligible for the specified level, false otherwise
     */
    public boolean isEligibleForLevel(String level) {
        if (studyYear <= 2) {
            return "Basic".equalsIgnoreCase(level);
        }
        return true; // Year 3+ can apply to all levels
    }

    /**
     * Checks if the student's major matches the internship's preferred major.
     *
     * @param preferredMajor the preferred major for the internship
     * @return true if the student's major matches, false otherwise
     */
    public boolean matchesMajor(String preferredMajor) {
        return this.major.equalsIgnoreCase(preferredMajor);
    }

    public void viewInternshipOpportunities(InternshipViewer internshipViewer) {
        internshipViewer.viewInternships();
    }

    /**
     * Submits an application for the specified internship.
     * Performs validation checks before submitting:
     * <ul>
     *   <li>Verifies student hasn't reached the 3 application limit</li>
     *   <li>Checks eligibility for the internship level</li>
     *   <li>Ensures the internship is accepting applications</li>
     *   <li>Prevents duplicate applications</li>
     * </ul>
     *
     * @param internship the internship to apply for
     * @return true if the application was successfully submitted, false otherwise
     */
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

    /**
     * Displays all internships the student has applied for, showing their status,
     * placement confirmation, withdrawal status, and company information.
     */
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

    /**
     * Accepts a placement offer for an approved application.
     * <ul>
     *   <li>Validates that the application belongs to this student</li>
     *   <li>Ensures the application status is SUCCESSFUL</li>
     *   <li>Prevents accepting multiple placements</li>
     *   <li>Automatically withdraws all other applications</li>
     * </ul>
     *
     * @param application the application to accept
     * @return true if the placement was successfully accepted, false otherwise
     */
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

    /**
     * Requests withdrawal for an application.
     * The withdrawal request must be approved by Career Centre Staff.
     *
     * @param application the application to withdraw
     * @return true if the withdrawal request was successfully submitted, false otherwise
     */
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