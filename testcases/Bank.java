// import com.vku.nframework.server.*;
// import com.vku.nframework.server.annotations.*; 

// @Path("/banking")
// public class Bank implements java.io.Serializable{
// @Path("/branchname")
// public String getBranchName(String city) throws BankingException {
// System.out.println("Method got called");
// System.out.println(this);

// if(city.equalsIgnoreCase("Ujjain")){
// return "Freeganj";
// }
// else if(city.equalsIgnoreCase("Indore")){
// return "Vijay Nagar";
// }
// else if(city.equalsIgnoreCase("Bhopal")){
// return "New Market";
// }
// else {
//     throw new BankingException("No branch in that city");
// }
// }

// public static void main(String gg[]){
// NFrameworkServer server = new NFrameworkServer();
// server.registerClass(Bank.class);
// server.start();
// }
// }


import com.vku.nframework.server.*;
import com.vku.nframework.server.annotations.*;

@Path("/banking")
public class Bank implements java.io.Serializable {

    // Step 1: Create a static instance of the class
    private static final Bank instance = new Bank();

    // Step 2: Private constructor to prevent direct instantiation
    private Bank() {}

    // Step 3: Factory method to return the singleton instance
    public static Bank getInstance() {
        return instance;
    }

    @Path("/branchname")
    public String getBranchName(String city) throws BankingException {
        System.out.println("Method got called");
        System.out.println(this);

        if (city.equalsIgnoreCase("Ujjain")) {
            return "Freeganj";
        } else if (city.equalsIgnoreCase("Indore")) {
            return "Vijay Nagar";
        } else if (city.equalsIgnoreCase("Bhopal")) {
            return "New Market";
        } else {
            throw new BankingException("No branch in that city");
        }
    }

    public static void main(String[] args) {
        NFrameworkServer server = new NFrameworkServer();

        // Step 4: Register the singleton instance instead of the class
        server.registerClass(Bank.class);
        server.start();
    }
}
