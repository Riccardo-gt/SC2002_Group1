import java.time.LocalDate;
import java.util.List;

public class Internship {
    String title;
    String description;
    String level; // Basic, Intermediate, Advanced
    String preferredMajor;
    LocalDate openingDate;
    LocalDate closingDate;
    String status; // Pending, Approved, Rejected, Filled
    boolean isVisible;
    CompanyRepresentative representative;
    int slots;
    List<Application> applications;

    public Internship() {

    }

    public String getTitle() {
        return this.title;
    }

    public String getPreferredMajor() {
        return this.preferredMajor;
    }

    public CompanyRepresentative getCompanyRepresentative() {
        return this.representative;
    }

    public List<Application> getApplications() {
    return applications;
}

    public void printApplications() {
        for (Application application : applications) {
            System.out.println(application);
        }
    }

    public void addApplication(Application application) {

    }

    public void setStatus(String status) {

    }

    public void toggleVisibility(boolean isVisible) {

    }

    public void checkFilledStatus() {

    }
}

