import java.util.*;
    
public class CompanyRepresentative extends User {
    String companyName = "", position = "", department = "", status = "";

    public CompanyRepresentative(String userId, String name, String email, String password, String companyName, String position, String department, String status) {
        super(userId, name, email, password);
        this.companyName = companyName;
        this.position = position;
        this.department = department;
        this.status = status;
    }

    void register() {

    }

    void createInternshipOpportunity() {
        
    }

    void viewApplications() {

        
    }
    void approveApplication() { // Should return boolean

    }

    void changeVisibility() {

    }
    
}

