package Models.Entities;

import Models.Utility_Classes.ApplicationViewer;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a company representative in the Internship Placement Management System.
 * Company representatives can create and manage internship opportunities, view applications,
 * and manage application statuses for their internships.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Can create up to 5 internship opportunities</li>
 *   <li>Must be approved by Career Centre Staff before accessing the system</li>
 *   <li>Can edit or delete only unapproved internships</li>
 *   <li>Can view and manage applications for their internships</li>
 *   <li>Can toggle visibility of their internships</li>
 * </ul>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public class CompanyRepresentative extends User {
    private String companyName;
    private String position;
    private String department;
    private String status;
    private static List<Internship> createdInternships = new ArrayList<>();
    private static List<CompanyRepresentative> allCompanyReps = new ArrayList<>();

    /**
     * Constructs a new CompanyRepresentative instance.
     * The new instance is immediately added to the static list {@code allCompanyReps}.
     *
     * @param userId The unique identifier for the representative.
     * @param name The full name of the representative.
     * @param email The email address of the representative.
     * @param password The password for the representative's account.
     * @param companyName The company the representative belongs to.
     * @param position The representative's job position.
     * @param department The representative's department.
     * @param status The initial authorization status of the account.
     */
    public CompanyRepresentative(String userId, String name, String email, String password, String companyName, String position, String department, String status) {
        super(userId, name, email, password);
        this.companyName = companyName;
        this.position = position;
        this.department = department;
        this.status = status;
        //this.createdInternships = new ArrayList<>();
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

    public void loadInternships(Internship internship) {
        createdInternships.add(internship);
    }

    /**
     * Creates a new internship opportunity by adding it to the list, provided
     * the maximum limit of 5 has not been reached.
     *
     * @param internship The {@link Internship} object to be created/added.
     */
    public void createInternshipOpportunity(Internship internship) {
        if (!canCreateInternship()) {
            System.out.println("Error: Maximum of 5 internships allowed per representative.");
            return;
        }
        createdInternships.add(internship);
        System.out.println("Internship opportunity created: " + internship.getTitle());
    }

    /**
     * Deletes an internship managed by this representative.
     * Deletion is prevented if the internship has an "Approved" status.
     *
     * @param internship The {@link Internship} object to delete.
     * @return {@code true} if the internship was successfully deleted; {@code false} otherwise.
     */
    public boolean deleteInternship(Internship internship) {
        if (!createdInternships.contains(internship)) {
            System.out.println("You do not manage this internship.");
            return false;
        }
        if ("Approved".equals(internship.getStatus())) {
            System.out.println("Cannot delete approved internships.");
            return false;
        }
        createdInternships.remove(internship);
        System.out.println("Internship deleted: " + internship.getTitle());
        return true;
    }

    /**
     * Edits an internship managed by this representative.
     * Editing is prevented if the internship has an "Approved" status.
     * Note: This method currently only performs validation and returns a boolean.
     * The actual editing logic (e.g., changing fields) would occur after a successful return.
     *
     * @param internship The {@link Internship} object to edit.
     * @return {@code true} if the internship is eligible for editing; {@code false} otherwise.
     */
    public boolean editInternship(Internship internship) {
        if (!createdInternships.contains(internship)) {
            System.out.println("You do not manage this internship.");
            return false;
        }
        if ("Approved".equals(internship.getStatus())) {
            System.out.println("Cannot edit approved internships.");
            return false;
        }
        return true;
    }

    /**
     * Displays all applications submitted for every internship created by this representative.
     * It utilizes the {@link ApplicationViewer} utility class.
     */
    public void viewApplications() {
        for (Internship internship : createdInternships) {
            ApplicationViewer viewer = new ApplicationViewer(internship);
            viewer.displayApplicants();
        }
    }

    /**
     * Changes the visibility status of an internship managed by this representative.
     *
     * @param internship The target {@link Internship} object.
     * @param visible {@code true} to make the internship visible; {@code false} to hide it.
     */
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