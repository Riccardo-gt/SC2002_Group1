package Models.Entities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a Career Centre Staff member in the Internship Placement Management System.
 * Staff members have administrative privileges to authorize company representatives,
 * approve internship opportunities, manage withdrawal requests, and generate reports.
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Authorize or reject company representative registrations</li>
 *   <li>Approve or reject internship opportunities</li>
 *   <li>Process student withdrawal requests</li>
 *   <li>Generate reports with various filters</li>
 * </ul>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public class CareerCentreStaff extends User {
    private String role;
    private String department;

    /**
     * Constructs a new CareerCentreStaff instance.
     *
     * @param userId The unique identifier for the staff member.
     * @param name The full name of the staff member.
     * @param email The email address of the staff member.
     * @param password The password for the staff member's account.
     * @param role The specific role of the staff member within the centre.
     * @param department The department the staff member belongs to.
     */
    public CareerCentreStaff(String userId, String name, String email, String password, String role, String department) {
        super(userId, name, email, password);
        this.role = role;
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public String getRole() {
        return role;

    }

    public boolean authorizeAccount(CompanyRepresentative representative) { // Should return boolean
        if (representative == null) {
            System.out.println("Invalid company representative.");
            return false;
        }
        System.out.println("Account authorized for: " + representative.getEmail());
        return true;
    }

    /**
     * Approves a submitted {@link Internship} opportunity.
     * This sets the internship's status to "Approved" and makes it visible to students.
     *
     * @param internship The internship to be approved.
     * @return {@code true} if the approval was successful; {@code false} if the internship is null.
     */
    public boolean approveInternshipOpportunity(Internship internship) { // Should return boolean
        if (internship == null) {
            System.out.println("Invalid internship.");
            return false;
        }
        internship.setStatus("Approved");
        internship.toggleVisibility(true);
        System.out.println("Internship approved: " + internship.getTitle());
        return true;
    }

    /**
     * Approves a student's withdrawal request for an {@link Application}.
     * This sets the application status to {@link Application.ApplicationStatus#WITHDRAWN}
     * and the withdrawal status to {@link Application.WithdrawalStatus#APPROVED}.
     *
     * @param application The application whose withdrawal is to be approved.
     * @return {@code true} if the approval was successful; {@code false} if the application is null.
     */
    public boolean approveWithdrawal(Application application) { // Should return boolean
        if (application == null) {
            System.out.println("Invalid application.");
            return false;
        }
        application.setStatus(Application.ApplicationStatus.WITHDRAWN);
        application.setWithdrawalStatus(Application.WithdrawalStatus.APPROVED);
        System.out.println("Withdrawal approved for: " + application.getStudent().getName());
        return true;
    }

    /**
     * Rejects a student's withdrawal request for an {@link Application}.
     * This sets the withdrawal status to {@link Application.WithdrawalStatus#REJECTED}
     * but leaves the application's primary status unchanged.
     *
     * @param application The application whose withdrawal is to be rejected.
     * @return {@code true} if the rejection was successful; {@code false} if the application is null.
     */
    public boolean rejectWithdrawal(Application application) { // Should return boolean
        if (application == null) {
            System.out.println("Invalid application.");
            return false;
        }
        application.setWithdrawalStatus(Application.WithdrawalStatus.REJECTED);
        System.out.println("Withdrawal rejected for: " + application.getStudent().getName());
        return true;
    }

    /**
     * Generates and displays a report of internships filtered by a specified criteria.
     * It uses inherited methods {@code filterInternships} and {@code displayInternships} from {@link User}.
     *
     * @param allInternships A list of all available {@link Internship} objects.
     * @param filter The specific value to filter by (e.g., "Approved" for status, "Junior" for level, "IT" for major).
     * @param filterType The type of filter to apply: 1 for status, 2 for level, 3 for major.
     */
    public void generateReport(List<Internship> allInternships, String filter, int filterType) {
        System.out.println("=== Career Centre Report ===");
        String filterTypeStr = "";
        switch (filterType) {
            case 1:
                filterTypeStr = "status";
                break;
            case 2:
                filterTypeStr = "level";
                break;
            case 3:
                filterTypeStr = "major";
                break;
            default:
                System.out.println("Invalid filter type.");
                return;
        }

        // Use the inherited filter method from User class
        List<Internship> filtered = filterInternships(allInternships, filterTypeStr, filter);

        // Use the inherited display method
        displayInternships(filtered, "Internships with " + filterTypeStr + " = '" + filter + "'");
    }

}
