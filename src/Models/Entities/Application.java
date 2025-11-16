package Models.Entities;

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

    public void confirmAcceptance() {
        if (this.status == ApplicationStatus.SUCCESSFUL) {
            setConfirmed(true);
            System.out.println("Acceptance confirmed for: " + internship.getTitle());
        } else {
            System.out.println("Cannot confirm - application not approved");
        }
    }
}
