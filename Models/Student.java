import java.util.*;
    
public class Student extends User {
    String major = "";
    int studyYear = 0;

    public Student(String userId, String name, String email, String password, String major, int studyYear) {
        super(userId, name, email, password);
        this.major = major;
        this.studyYear = studyYear;
    }

    void viewInternshipOpportunities() {

    }

    void applyForInternships() {
        
    }

    void viewAppliedInternships() {

    }

    void accept() {

    }

    void widthraw() {

    }
}

