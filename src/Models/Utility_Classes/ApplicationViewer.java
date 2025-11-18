package Models.Utility_Classes;

import Models.Entities.Application;
import Models.Entities.Internship;
import Models.Entities.Student;

import java.util.*;
import java.util.stream.Collectors;

public class ApplicationViewer {
    private Internship currentInternship;
    private List<Application> applications;
    private List<Application> rankedApplicants;

    public ApplicationViewer(Internship internship) {
        this.currentInternship = internship;
        this.applications = new ArrayList<>(internship.getApplications());
    }

    public List<Application> getRankedApplicants() {
        return rankedApplicants;
    }

    // Display all applicants for the current internship
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

    // Rank applicants based on criteria (GPA, major match, and year of study)
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

    private int calculateApplicantScore(Application application) {
        Student s = application.getStudent();
        int score = 0;

        // Higher GPA = better score
        score += (int) (s.getCGPA() * 10);

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

    // Reject an application
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