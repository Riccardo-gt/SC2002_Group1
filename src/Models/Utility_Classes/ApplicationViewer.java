package Models.Utility_Classes;

import Models.Entities.Application;
import Models.Entities.Internship;
import Models.Entities.Student;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for viewing and managing applications for a specific internship.
 * Provides functionality to display applicants, rank them based on qualifications,
 * and approve or reject applications.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Display all applicants for an internship</li>
 *   <li>Rank applicants based on CGPA and year of study</li>
 *   <li>Approve or reject applications</li>
 *   <li>Handle withdrawn applications appropriately</li>
 * </ul>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public class ApplicationViewer {
    private Internship currentInternship;
    private List<Application> applications;
    private List<Application> rankedApplicants;

    /**
     * Constructs a new ApplicationViewer for the specified internship.
     * Initializes the applications list from the internship.
     *
     * @param internship the internship to view applications for
     */
    public ApplicationViewer(Internship internship) {
        this.currentInternship = internship;
        this.applications = new ArrayList<>(internship.getApplications());
    }

    public List<Application> getRankedApplicants() {
        return rankedApplicants;
    }

    /**
     * Displays all applicants for the current internship.
     * Shows total application count and individual applicant details
     * including name and application status.
     */
    public void displayApplicants() {
        System.out.println("=== Applicants for: " + currentInternship.getTitle() + " ===");
        List<Application> activeApplications = applications.stream()
                .filter(app -> app.getStatus() != Application.ApplicationStatus.WITHDRAWN)
                .collect(Collectors.toList());

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

    /**
     * Ranks applicants based on their qualifications.
     * Excludes withdrawn applications and confirmed placements.
     *
     * <p>Ranking criteria:</p>
     * <ul>
     *   <li>CGPA (weighted by factor of 10)</li>
     *   <li>Year of study (weighted by factor of 5)</li>
     * </ul>
     *
     * <p>Displays ranked list with score, status, CGPA, major, year, and withdrawal status.</p>
     */
    public void rankApplicants() {
        System.out.println("=== Ranked Applicants for: " + currentInternship.getTitle() + " ===");

        List<Application> activeApplications = applications.stream()
                .filter(app -> app.getStatus() != Application.ApplicationStatus.WITHDRAWN && !app.isConfirmed())
                .collect(Collectors.toList());

        if (activeApplications.isEmpty()) {
            System.out.println("No active applications.");
            this.rankedApplicants = new ArrayList<>(); // Initialize empty list
            return;
        }

        this.rankedApplicants = applications.stream()
                .sorted((a1, a2) -> Integer.compare(
                        calculateApplicantScore(a2), calculateApplicantScore(a1)))
                .collect(Collectors.toList());

        int rank = 1;
        for (Application app : rankedApplicants) {
            Student student = app.getStudent();
            int score = calculateApplicantScore(app);
            System.out.println(rank + ". " + student.getName() +
                    " | Score: " + score +
                    " | Status: " + app.getStatus() +
                    " | CGPA: " + student.getCGPA() +
                    " | Major: " + student.getMajor() +
                    " | Year: " + student.getYearOfStudy() +
                    " | Withdrawal: " + app.getWithdrawalStatus());
            rank++;
        }
    }

    /**
     * Calculates a score for an applicant based on their qualifications.
     * Higher scores indicate better qualified candidates.
     *
     * <p>Scoring formula:</p>
     * <ul>
     *   <li>CGPA × 10</li>
     *   <li>Year of study × 5</li>
     * </ul>
     *
     * @param application the application to score
     * @return the calculated score
     */
    private int calculateApplicantScore(Application application) {
        Student s = application.getStudent();
        int score = 0;

        // Higher GPA = better score
        score += (int) (s.getCGPA() * 10);

        // Higher study year = more experience
        score += s.getYearOfStudy() * 5;

        return score;
    }

    /**
     * Approves an application for the internship.
     * Validates that the application exists, hasn't been withdrawn,
     * and doesn't have a pending withdrawal request.
     *
     * @param application the application to approve
     * @return true if the application was successfully approved, false otherwise
     */
    public boolean approveApplication(Application application) {
        if (!applications.contains(application)) {
            System.out.println("Application not found for this internship.");
            return false;
        }

        if (application.getStatus() == Application.ApplicationStatus.WITHDRAWN) {
            System.out.println("Cannot manage application: it has already been withdrawn.");
            return false;
        }
        if (application.getWithdrawalStatus() == Application.WithdrawalStatus.PENDING) {
            System.out.println("Cannot reject application: student has requested withdrawal (pending).");
            return false;
        }

        if (application.getStatus() == Application.ApplicationStatus.SUCCESSFUL) {
            System.out.println("Application is already successful.");
            return true;
        }

        application.setStatus(Application.ApplicationStatus.SUCCESSFUL);
        System.out.println("Application successful for: " + application.getStudent().getName());
        return true;
    }

    /**
     * Rejects an application for the internship.
     * Validates that the application exists before rejecting.
     *
     * @param application the application to reject
     * @return true if the application was successfully rejected, false otherwise
     */
    public boolean rejectApplication(Application application) {
        if (!applications.contains(application)) {
            System.out.println("Application not found for this internship.");
            return false;
        }

        if (application.getStatus() == Application.ApplicationStatus.UNSUCCESSFUL) {
            System.out.println("Application is already unsuccessful.");
            return true;
        }

        application.setStatus(Application.ApplicationStatus.UNSUCCESSFUL);
        System.out.println("Application unsucessful for: " + application.getStudent().getName());
        return true;
    }
}