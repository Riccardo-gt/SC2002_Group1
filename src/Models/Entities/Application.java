package Models.Entities;

/**
 * Represents an application submitted by a student for an internship opportunity.
 * Tracks the application status, withdrawal status, and placement confirmation.
 *
 * <p>An application progresses through various states:</p>
 * <ul>
 *   <li>PENDING: Initial state when application is submitted</li>
 *   <li>SUCCESSFUL: Approved by company representative</li>
 *   <li>UNSUCCESSFUL: Rejected by company representative</li>
 *   <li>WITHDRAWN: Student withdrew the application</li>
 * </ul>
 *
 * @author SC2002_Group1
 * @version 1.0
 * @since 2025-11-18
 */
public class Application {
    private Student student;
    private Internship internship;

    public enum ApplicationStatus {
        PENDING,
        SUCCESSFUL,
        UNSUCCESSFUL,
        WITHDRAWN
    }

    public enum WithdrawalStatus {
        NOTREQUESTED,
        PENDING,
        APPROVED,
        REJECTED
    }

    private ApplicationStatus status; // Pending, Successful, Unsuccessful
    private WithdrawalStatus withdrawalStatus;
    private boolean confirmed;

    /**
     * Constructs a new Application instance.
     * The initial status is set to {@link ApplicationStatus#PENDING},
     * {@code confirmed} is set to {@code false}, and
     * {@code withdrawalStatus} is set to {@link WithdrawalStatus#NOTREQUESTED}.
     *
     * @param student The student submitting the application.
     * @param internship The internship the student is applying for.
     */
    public Application(Student student, Internship internship) {
        this.student = student;
        this.internship = internship;
        this.status = ApplicationStatus.PENDING;
        this.confirmed = false;
        this.withdrawalStatus = WithdrawalStatus.NOTREQUESTED;
    }

    public Internship getInternship() {
        return internship;
    }

    public ApplicationStatus getStatus() {
        return this.status;
    }

    public WithdrawalStatus getWithdrawalStatus() {
        return this.withdrawalStatus;
    }

    public Student getStudent() {
        return this.student;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
        this.withdrawalStatus = withdrawalStatus;
    }

    /**
     * Attempts to confirm the student's acceptance of the internship offer.
     * This operation is only permitted if the current application status is
     * {@link ApplicationStatus#SUCCESSFUL}.
     * If successful, the {@code confirmed} flag is set to {@code true}.
     */
    public void confirmAcceptance() {
        if (this.status == ApplicationStatus.SUCCESSFUL) {
            setConfirmed(true);
            System.out.println("Acceptance confirmed for: " + internship.getTitle());
        } else {
            System.out.println("Cannot confirm - application not approved");
        }
    }
}
