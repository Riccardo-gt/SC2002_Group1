import java.util.ArrayList;
import java.util.List;

public class InternshipViewer {
    List<Internship> internships;
    public InternshipViewer() {
        internships = new ArrayList<Internship>();
    }

    public List<Internship> getInternships() {
        return internships;
    }

    public void addInternship(Internship internship) {
        internships.add(internship);
    }

    public void viewInternships() {
        for (Internship internship: internships) {
            System.out.println(internship);
        }
    }
    
    public void applyForInternship(Application application, Internship internship) {
        CompanyRepresentative representative = internship.getCompanyRepresentative();
        representative.approveApplication(application);
    }

    public void requestWithdrawal(Student student, Application application) {
        String major = student.getMajor();
        CareerCentreStaff approvingStaff = null;

        for (CareerCentreStaff staff: staffList) { // staffList = temp placeholder
            if (staff.department == majorToDepartment.get(major)) { // majorToDepartment = temp placeholder
                approvingStaff = staff;
                break;
            }
        }

        approvingStaff.approveWithdrawal(application);
    }


}
