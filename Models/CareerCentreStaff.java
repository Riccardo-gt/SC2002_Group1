import java.util.*;
    
public class CareerCentreStaff extends User {
    String role = "", department = "";

    public CareerCentreStaff(String userId, String name, String email, String password, String role, String department) {
        super(userId, name, email, password);
        this.role = role;
        this.department = department;
    }

    void authorizeAccount() { // Should return boolean
        
    }

    void approveInternshipOpportunity() { // Should return boolean
        
    }

    void approveWithdrawal(Application application) { // Should return boolean
    }

    void generateReport() { 

    }
    
}

