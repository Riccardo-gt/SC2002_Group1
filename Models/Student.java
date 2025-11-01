import java.util.*;
    
public class Student extends User {
    String major = "";
    int studyYear = 0;
    float cgpa;
    List<Application> applications;

    public Student(String userId, String name, String email, String password, String major, int studyYear, float cgpa) {
        super(userId, name, email, password);
        this.major = major;
        this.studyYear = studyYear;
        this.cgpa = cgpa;
        applications = new ArrayList<Application>();
    }

    String getMajor() {
        return this.major;
    }

    void viewInternshipOpportunities() {
        InternshipViewer internshipViewer = new InternshipViewer();
        internshipViewer.viewInternships();
    }

    void applyForInternship() {
        InternshipViewer internshipViewer = new InternshipViewer();
        List<Internship> internships = internshipViewer.getInternships();
        Internship internship = internships.get(new Random().nextInt(internships.size()));
        Application application = new Application(this, internship);
        internshipViewer.applyForInternship(application, internship);
    }

    void viewAppliedInternships() {
        for (Application application: applications) { 
            System.out.println(application);
        }
    }

    void accept(Internship internship) {
        Application relevantApplication = null;
        
        for (Application application: applications) { 
            if (application.getInternship() == internship) {
                relevantApplication = application;
            }
        }

        relevantApplication.confirmAcceptance();
    }

    void widthraw() {
        InternshipViewer internshipViewer = new InternshipViewer();
        Application application = applications.get(new Random().nextInt(applications.size()));
        internshipViewer.requestWithdrawal(this, application);
    }
}

