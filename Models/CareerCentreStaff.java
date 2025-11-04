    
public class CareerCentreStaff extends User {
    private String role;
    private String department;

    public CareerCentreStaff(String userId, String name, String email, String password, String role, String department) {
        super(userId, name, email, password);
        this.role = role;
        this.department = department;
    }

    public void authorizeAccount() { // Should return boolean
        
    }

    public void approveInternshipOpportunity() { // Should return boolean
        
    }

    public void approveWithdrawal(Application application) { // Should return boolean
    }

    public void generateReport() { 

    }
    
}

