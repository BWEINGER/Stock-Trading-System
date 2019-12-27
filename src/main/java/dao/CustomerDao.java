package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Customer;
import model.Location;
import model.Person;

public class CustomerDao {
	/*
	 * This class handles all the database operations related to the customer table
	 */

    public Customer getDummyCustomer() {
        Location location = new Location();
        location.setZipCode(11790);
        location.setCity("Stony Brook");
        location.setState("NY");

        Customer customer = new Customer();
        customer.setId("111-11-1111");
        customer.setAddress("123 Success Street");
        customer.setLastName("Lu");
        customer.setFirstName("Shiyong");
        customer.setEmail("shiyong@cs.sunysb.edu");
        customer.setLocation(location);
        customer.setTelephone("5166328959");
        customer.setCreditCard("1234567812345678");
        customer.setRating(1);

        return customer;
    }
    public List<Customer> getDummyCustomerList() {
        /*Sample data begins*/
        List<Customer> customers = new ArrayList<Customer>();

        for (int i = 0; i < 10; i++) {
            customers.add(getDummyCustomer());
        }
		/*Sample data ends*/

        return customers;
    }

    /**
	 * @param String searchKeyword
	 * @return ArrayList<Customer> object
	 */
	public List<Customer> getCustomers(String searchKeyword) {
            if(searchKeyword == null) {
            return getAllCustomers();
            }
		/*
		 * This method fetches one or more customers based on the searchKeyword and returns it as an ArrayList
		 */
		

		/*
		 * The students code to fetch data from the database based on searchKeyword will be written here
		 * Each record is required to be encapsulated as a "Customer" class object and added to the "customers" List
		 */
		
		List<Customer> customers = new ArrayList<Customer>();
                try{
                    
                    System.out.println("Getting customers with keyword " + searchKeyword);
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT *\n" +
                    "FROM Client C, Person P, Account A\n" +
                    "WHERE P.SSN = C.Id AND A.Client = C.Id AND (P.FirstName like \'%" + searchKeyword + "%\' OR P.LastName like \'%" + searchKeyword + "%\')");

                    //note: we assume that we are seting all accont ids to 4, 

                    while(rs.next()){
                        Customer cust = new Customer();
                        cust.setClientId(rs.getString("Id"));
                        cust.setCreditCard(rs.getString("CreditCardNumber"));
                        cust.setAccountCreationTime(rs.getString("DateOpened"));
                        //cust.setAccountNumber(rs.getInt("A.Id")); // not sure if this will work
                        cust.setRating(rs.getInt("Rating"));


                        cust.setFirstName(rs.getString("FirstName"));
                        cust.setLastName(rs.getString("LastName"));
                        cust.setAddress(rs.getString("Address"));
                        cust.setId(rs.getString("Id"));

                        Location loc = new Location();
                        loc.setCity(rs.getString("City"));
                        loc.setZipCode(rs.getInt("ZipCode"));
                        loc.setState(rs.getString("State"));

                        cust.setLocation(loc);
                        cust.setSsn(rs.getString("SSN"));
                        cust.setTelephone(rs.getString("Telephone"));
                        //what about account creation time

                        customers.add(cust);
                    }        
                }
                catch(Exception e){
                    System.out.println(e);
                }


                return customers;

	}


	public Customer getHighestRevenueCustomer() {
		/*
		 * This method fetches the customer who generated the highest total revenue and returns it
		 * The students code to fetch data from the database will be written here
		 * The customer record is required to be encapsulated as a "Customer" class object
		 */

		Customer cust = new Customer();
                
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT R1.Id, R1.Revenue\n" +
                        "FROM\n" +
                        "	((SELECT C.ID, sum(TR.Fee) as Revenue\n" +
                        "	FROM Transaction TR, Trade T, Account A, Client C\n" +
                        "	WHERE TR.Id = T.TransactionId AND A.Id = T.AccountId AND C.Id = A.Client\n" +
                        "	GROUP BY C.Id) as R1,\n" +
                        "    (SELECT C.ID, sum(TR.Fee) as Revenue\n" +
                        "	FROM Transaction TR, Trade T, Account A, Client C\n" +
                        "	WHERE TR.Id = T.TransactionId AND A.Id = T.AccountId AND C.Id = A.Client\n" +
                        "	GROUP BY C.Id) as R2\n" +
                        "    )\n" +
                        "WHERE R1.Revenue > R2.Revenue;");
                    
                    //should only be one element
                    //now we use the getCustomer method
                    rs.next();
                    cust = getCustomer(rs.getString("Id"));
                }
                catch(Exception e){
                    System.out.println(e);
                }
                

		return cust;
	}

	public Customer getCustomer(String customerID) {

		/*
		 * This method fetches the customer details and returns it
		 * customerID, which is the Customer's ID who's details have to be fetched, is given as method parameter
		 * The students code to fetch data from the database will be written here
		 * The customer record is required to be encapsulated as a "Customer" class object
		 */
		
		Customer cust = new Customer();
                
                try{
                    System.out.println("Running getCustomer... " + customerID);
                    String idNum = customerID.replaceAll("-", "");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * \n" +
                    "FROM Client C, Person P, Account A, Location l\n" +
                    "WHERE P.SSN = C.Id AND C.Id = "+customerID+" AND A.Client = C.Id AND L.ZipCode = P.ZipCode;");
                    rs.next();
                    cust.setClientId(rs.getString("Id"));
                    cust.setCreditCard(rs.getString("CreditCardNumber"));
                    cust.setAccountCreationTime(rs.getString("DateOpened"));
                    cust.setEmail(rs.getString("Email"));
                    cust.setRating(rs.getInt("Rating"));
                    //added
                    cust.setAccountNumber(rs.getInt("A.Id"));
                    
                    cust.setFirstName(rs.getString("FirstName"));
                    cust.setLastName(rs.getString("LastName"));
                    cust.setAddress(rs.getString("Address"));
                    cust.setId(rs.getString("Id"));
                    
                    Location loc = new Location();
                    loc.setCity(rs.getString("City"));
                    loc.setZipCode(rs.getInt("ZipCode"));
                    loc.setState(rs.getString("State"));
                    
                    cust.setLocation(loc);
                    cust.setSsn(rs.getString("SSN"));
                    cust.setTelephone(rs.getString("Telephone"));
                   
                }
                catch(Exception e){
                    System.out.println(e);
                }
                
                
		return cust;

	}
	
	public String deleteCustomer(String customerID) {

		/*
		 * This method deletes a customer returns "success" string on success, else returns "failure"
		 * The students code to delete the data from the database will be written here
		 * customerID, which is the Customer's ID who's details have to be deleted, is given as method parameter
		 */

		 try{
                     System.out.println(customerID); //customer id is null
                    String idNum = customerID.replaceAll("-", "");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("Select Id\n" +
                            "From Account\n" +
                            "Where Client = " + idNum);
                    rs.next();
                    int accountNum = rs.getInt("Id");
                    st.executeUpdate("Delete FROM HasStock\n" +
                    "Where AccountID = " + accountNum);
                    st.executeUpdate("Delete FROM Trade\n" +
                    "Where AccountId = " + accountNum);
                    st.executeUpdate("DELETE FROM Account\n" +
                    "WHERE Client = "+idNum);
                    st.executeUpdate(
                    "Delete from Client\n" +
                    "Where Id = "+idNum);
                   
                }
                catch(Exception e){
                    System.out.println(e);
                }
                
                
		/*Sample data begins*/
		return "success";
		/*Sample data ends*/

		
	}


	public String getCustomerID(String email) {
		/*
		 * This method returns the Customer's ID based on the provided email address
		 * The students code to fetch data from the database will be written here
		 * username, which is the email address of the customer, who's ID has to be returned, is given as method parameter
		 * The Customer's ID is required to be returned as a String
		 */

		String res = "";
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT SSN\n" +
                    "FROM Person\n" +
                    "WHERE Email = '"+email+"'");
                    
                    rs.next();
                    res = rs.getString("SSN");
                   
                }
                catch(Exception e){
                    System.out.println(e);
                }
                if(res.equals("")) {
                    System.out.println("Using default customerid");
                    return "111-11-1111";
                }
                return res;
	}


	public String addCustomer(Customer customer) {

		/*
		 * All the values of the add customer form are encapsulated in the customer object.
		 * These can be accessed by getter methods (see Customer class in model package).
		 * e.g. firstName can be accessed by customer.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database insertion of the customer details and return "success" or "failure" based on result of the database insertion.
		 */
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    try {
                    st.executeUpdate("Insert into Location\n" +
                    "Values ("+customer.getLocation().getZipCode()+", '"+customer.getLocation().getCity()+"', '"+customer.getLocation().getState()+"')");
                    } catch(Exception e) {
                        System.out.println("Location already in table - OK");
                    }
                    try {
                    st.executeUpdate("Insert into Person\n" +
                    "Values ("+customer.getSsn().replaceAll("-", "")+", '"+customer.getLastName()+"', '"+customer.getFirstName()+"', '"+customer.getAddress()+"', "+customer.getLocation().getZipCode()+", "+customer.getTelephone().replaceAll("-", "")+", '" + customer.getEmail() + "')");
                    } catch(Exception e) {
                        System.out.println("Person already in table - OK");
                    }
                    try {
                        System.out.println(customer.getRating() + ", " + customer.getCreditCard() + ", " + customer.getClientId());
                    st.executeUpdate("Insert into Client\n" +
                    "Values("+customer.getRating()+", "+customer.getCreditCard().replaceAll("-", "")+", "+customer.getSsn().replaceAll("-", "")+")");
                    } catch(Exception e) {
                        System.out.println(e);
                        System.out.println("Failure inserting client.... not OK.");
                        return "failure";
                    }
                    //Get account id
                    System.out.println("Account id is " + customer.getAccountNumber());
                    ResultSet maxId = st.executeQuery("Select MAX(Id) as maxId\n" +
                            "From Account");
                    int nextId = 1;
                    if(maxId.next()) {
                        nextId = maxId.getInt("maxId") + 1;
                    }
                    //Get date
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    System.out.println("Client id: " + customer.getClientId());
                    st.executeUpdate("Insert into Account\n" +
                    "Values ("+nextId+", '"+dateFormat.format(date)+"', "+customer.getSsn().replaceAll("-", "")+");");
                   
                }
                catch(Exception e){
                    System.out.println(e);
                }
		
		/*Sample data begins*/
		return "success";
		/*Sample data ends*/


	}

	public String editCustomer(Customer customer) {
		/*
		 * All the values of the edit customer form are encapsulated in the customer object.
		 * These can be accessed by getter methods (see Customer class in model package).
		 * e.g. firstName can be accessed by customer.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database update and return "success" or "failure" based on result of the database update.
		 */
		
		 try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    try {
                    st.executeUpdate("Update Location\n" +
                    "Set City = '"+customer.getLocation().getCity()+"', State = '"+customer.getLocation().getState()+"', ZipCode = "+customer.getLocation().getZipCode()+"\n" +
                    "Where Zipcode = "+customer.getLocation().getZipCode()+";");
                    } catch(Exception e) {
                        System.out.println(e);
                        System.out.println("Location not updated");
                    }
                    try {
                    st.executeUpdate("Update Person\n" +
                    "Set LastName = '"+customer.getLastName()+"', FirstName = '"+customer.getFirstName()+"', Address = '"+customer.getAddress()+"', Telephone = "+customer.getTelephone().replaceAll("-", "")+", Email = '"+customer.getEmail()+"'\n" +
                    "Where SSN= "+customer.getSsn()+";");
                    } catch(Exception e) {
                        System.out.println(e);
                        System.out.println("Person not updated");
                    }
                    try {
                    st.executeUpdate("Update Client\n" +
                    "Set Rating = "+customer.getRating()+", CreditCardNumber = "+customer.getCreditCard().replaceAll("-", "")+"\n" +
                    "Where Id = "+customer.getSsn()+";");
                    } catch(Exception e) {
                        System.out.println(e);
                        System.out.println("Client not updated");
                    }
                    //Is updating account ID necessary?
                    /*st.executeUpdate("Update Account\n" +
                    "Set Id = 4\n" +
                    "Where Client = "+customer.getClientId()+";");*/
                   
                }
                catch(Exception e){
                    System.out.println(e);
                }
                
                
		/*Sample data begins*/
		return "success";
		/*Sample data ends*/


	}

    public List<Customer> getCustomerMailingList() {
		/*
		 * This method fetches the all customer mailing details and returns it
		 * The students code to fetch data from the database will be written here
		 */
        List<Customer> customers = new ArrayList<Customer>();
        
		/*
		 * This method fetches the all customer mailing details and returns it
		 * The students code to fetch data from the database will be written here
		 */
                
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT P.SSN, P.LastName, P.FirstName, P.Address, P.Telephone, P.Email, L.Zipcode, L.City, L.State\n" +
                    "FROM Client C, PERSON P, Location L\n" +
                    "WHERE C.ID = P.SSN AND P.ZipCode = L.ZipCode;");
                    
                    while(rs.next()){
                        Customer cust = new Customer();


                        cust.setFirstName(rs.getString("FirstName"));
                        cust.setLastName(rs.getString("LastName"));
                        cust.setAddress(rs.getString("Address"));
                        cust.setEmail(rs.getString("Email"));

                        Location loc = new Location();
                        loc.setCity(rs.getString("City"));
                        loc.setZipCode(rs.getInt("ZipCode"));
                        loc.setState(rs.getString("State"));

                        cust.setLocation(loc);
                        cust.setSsn(rs.getString("SSN"));
                        cust.setId(rs.getString("SSN"));
                        cust.setTelephone(rs.getString("Telephone"));
                        cust.setAddress(rs.getString("Address"));

                        customers.add(cust);
                    } 
                }
                catch(Exception e){
                    System.out.println(e);
                }
      

        return customers;
    }

    public List<Customer> getAllCustomers() {
        /*
	* This method fetches returns all customers
	*/
        List<Customer> customers = new ArrayList<Customer>();
        try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT P.SSN, P.LastName, P.FirstName, P.Address, P.Telephone, L.Zipcode, L.City, L.State, P.Email, C.CreditCardNumber, C.Rating\n" +
                    "FROM Client C, PERSON P, Location L\n" +
                    "WHERE C.ID = P.SSN AND P.ZipCode = L.ZipCode;");
                    
                    while(rs.next()){
                        Customer cust = new Customer();


                        cust.setFirstName(rs.getString("FirstName"));
                        cust.setLastName(rs.getString("LastName"));
                        cust.setAddress(rs.getString("Address"));
                        cust.setEmail(rs.getString("Email"));

                        Location loc = new Location();
                        loc.setCity(rs.getString("City"));
                        loc.setZipCode(rs.getInt("ZipCode"));
                        loc.setState(rs.getString("State"));

                        cust.setLocation(loc);
                        cust.setSsn(rs.getString("SSN"));
                        cust.setId(rs.getString("SSN"));
                        cust.setTelephone(rs.getString("Telephone"));
                        cust.setAddress(rs.getString("Address"));
                        cust.setCreditCard(rs.getString("CreditCardNumber"));
                        cust.setRating(rs.getInt("Rating"));

                        customers.add(cust);
                    } 
                }
                catch(Exception e){
                    System.out.println(e);
                }
      

        return customers;

    }
}
