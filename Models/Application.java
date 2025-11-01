public class Application {
    Student student;
    Internship internship;
    String status; // Pending, Successful, Unsuccessful
    boolean confirmed;

    public Application(Student student, Internship internship) {
        this.student = student;
        this.internship = internship;
        this.status = "Pending";
        this.confirmed = false;
    }

    public Internship getInternship() {
        return internship;
    }

    public void setStatus(String status) {

    }
    
    public void confirmAcceptance() { 

    }
}

