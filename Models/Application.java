public class Application {
    private Student student;
    private Internship internship;
    public enum ApplicationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
    private ApplicationStatus status; // Pending, Successful, Unsuccessful
    private boolean confirmed;

    public Application(Student student, Internship internship) {
        this.student = student;
        this.internship = internship;
        this.status = ApplicationStatus.PENDING;
        this.confirmed = false;
    }
    

    public Internship getInternship() {
        return internship;
    }

    public ApplicationStatus getStatus() {
        return this.status;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void confirmAcceptance() {
        if (this.status == ApplicationStatus.APPROVED) {
            this.confirmed = true;
            System.out.println("Acceptance confirmed for: " + internship.getTitle());
        } else {
            System.out.println("Cannot confirm - application not approved");
        }
    }
}

