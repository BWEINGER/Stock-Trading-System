package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Employee;
import model.Location;

public class EmployeeDao {
	/*
	 * This class handles all the database operations related to the employee table
	 */

    public Employee getDummyEmployee()
    {
        Employee employee = new Employee();

        Location location = new Location();
        location.setCity("Stony Brook");
        location.setState("NY");
        location.setZipCode(11790);

		/*Sample data begins*/
        employee.setEmail("shiyong@cs.sunysb.edu");
        employee.setFirstName("Shiyong");
        employee.setLastName("Lu");
        employee.setLocation(location);
        employee.setAddress("123 Success Street");
        employee.setStartDate("2006-10-17");
        employee.setTelephone("5166328959");
        employee.setEmployeeID("631-413-5555");
        employee.setHourlyRate(100);
		/*Sample data ends*/

        return employee;
    }

    public List<Employee> getDummyEmployees()
    {
       List<Employee> employees = new ArrayList<Employee>();

        for(int i = 0; i < 10; i++)
        {
            employees.add(getDummyEmployee());
        }

        return employees;
    }

	public String addEmployee(Employee employee) {

		/*
		 * All the values of the add employee form are encapsulated in the employee object.
		 * These can be accessed by getter methods (see Employee class in model package).
		 * e.g. firstName can be accessed by employee.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database insertion of the employee details and return "success" or "failure" based on result of the database insertion.
		 */
		
            try {
		String empSSN = employee.getSsn();
                String ssnNumber = empSSN.replaceAll("-", "");
                String idNum = empSSN.replaceAll("-", "");
                String phoneNumber = employee.getTelephone();
                String phone = phoneNumber.replaceAll("-", "");
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                Statement st = con.createStatement();
                try {
                st.executeUpdate("INSERT INTO Location\n" +
                                 "Values("+employee.getLocation().getZipCode()+", '"+employee.getLocation().getCity()+"', '"+employee.getLocation().getState() + "')");
                } catch(Exception e) {
                System.out.println("Location not added - OK");
                }
                try {
                    System.out.println(ssnNumber);
                    System.out.println(employee.getLastName());
                    System.out.println(employee.getFirstName());
                    System.out.println(employee.getAddress());
                    System.out.println(employee.getLocation().getZipCode());
                    System.out.println(phone);
                    System.out.println(employee.getEmail());
                st.executeUpdate("INSERT INTO PERSON\n" +
                                 "Values(" + ssnNumber + ", '" + employee.getLastName() + "', '" + employee.getFirstName() + "', '" + employee.getAddress() + "', " + employee.getLocation().getZipCode() + ", " + phone + ", '" + employee.getEmail() + "')");
                } catch(Exception e) {
                    System.out.println(e);
                System.out.println("Person not added - OK");
                }
                try {
                st.executeUpdate("INSERT INTO Employee\n" +
                                 "Values(" + idNum + ", " + ssnNumber + ", CONVERT('" + employee.getStartDate() + "', DATE), " + employee.getHourlyRate() + ")");
                } catch(Exception e) {
                return "failure";
                }
            } catch(Exception e) {
                    System.out.println(e);
                    return "failure";
                }
                return "success";
	}

	public String editEmployee(Employee employee) {
		/*
		 * All the values of the edit employee form are encapsulated in the employee object.
		 * These can be accessed by getter methods (see Employee class in model package).
		 * e.g. firstName can be accessed by employee.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database update and return "success" or "failure" based on result of the database update.
		 */
		try{
                    String empSSN = employee.getSsn();
                    int ssnNumber = Integer.parseInt(empSSN.replaceAll("-", ""));
                    String empID = employee.getSsn();
                    String idNum = empSSN.replaceAll("-", "");
                    String phoneNumber = employee.getTelephone();
                    String phone = phoneNumber.replaceAll("-", "");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    st.executeUpdate("Update Location\n" +
                    "Set City = '"+employee.getLocation().getCity()+"', State = '"+employee.getLocation().getState()+"', ZipCode = "+employee.getLocation().getZipCode()+"\n" +
                    "Where Zipcode = "+employee.getLocation().getZipCode()+";");
                    st.executeUpdate("Update Person\n" +
                    "Set LastName = '"+employee.getLastName()+"', FirstName = '"+employee.getFirstName()+"', Address = '"+employee.getAddress()+"', Telephone = "+ phone +", Email = '" + employee.getEmail() + "'\n" +
                    "Where SSN= "+ ssnNumber +";");
                    st.executeUpdate("Update Employee\n" +
                    "Set HourlyRate = "+ employee.getHourlyRate() +", StartDate = CONVERT('" + employee.getStartDate() + "', DATE)\n" +
                    "Where SSN = "+ ssnNumber +";");
                    //st.executeUpdate("Update Login\n" +
                    //"Set Username = '" + employee.getEmail() + "'\n" +
                    //"Where ???")
                    
                }
                catch(Exception e){
                    System.out.println(e);
                }

		/*Sample data begins*/
		return "success";
		/*Sample data ends*/

	}

	public String deleteEmployee(String employeeID) {
		/*
		 * employeeID, which is the Employee's ID which has to be deleted, is given as method parameter
		 * The sample code returns "success" by default.
		 * You need to handle the database deletion and return "success" or "failure" based on result of the database deletion.
		 */
		
		 try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    st.executeUpdate("DELETE FROM Employee\n" + 
                                     "WHERE ID = "+employeeID.replaceAll("-", ""));
  
                }
                catch(Exception e){
                    System.out.println(e);
                }
		
		/*Sample data begins*/
		return "success";
		/*Sample data ends*/

	}

	
	public List<Employee> getEmployees() {

		/*
		 * The students code to fetch data from the database will be written here
		 * Query to return details about all the employees must be implemented
		 * Each record is required to be encapsulated as a "Employee" class object and added to the "employees" List
		 */

		List<Employee> employees = new ArrayList<Employee>();
                
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * \n" +
                    "FROM Employee E, Person P, Location L\n" +
                    "WHERE E.SSN = P.SSN AND L.ZipCode = P.ZipCode");
                    
                    while(rs.next()){
                        Employee emp = new Employee();
                        emp.setEmployeeID(rs.getString("Id"));
                        emp.setStartDate(rs.getString("StartDate"));
                        emp.setHourlyRate(rs.getInt("HourlyRate"));

                        emp.setFirstName(rs.getString("FirstName"));
                        emp.setLastName(rs.getString("LastName"));
                        emp.setAddress(rs.getString("Address"));
                        emp.setId(rs.getString("Id"));
                        emp.setEmail(rs.getString("Email"));
                        
                        Location loc = new Location();
                        loc.setCity(rs.getString("City"));
                        loc.setZipCode(rs.getInt("ZipCode"));
                        loc.setState(rs.getString("State"));

                        emp.setLocation(loc);
                        emp.setSsn(rs.getString("SSN"));
                        emp.setTelephone(rs.getString("Telephone"));

                        employees.add(emp);
                    }        
  
                }
                catch(Exception e){
                    System.out.println(e);
                }

		
		return employees;

	}

	public Employee getEmployee(String employeeID) {

		/*
		 * The students code to fetch data from the database based on "employeeID" will be written here
		 * employeeID, which is the Employee's ID who's details have to be fetched, is given as method parameter
		 * The record is required to be encapsulated as a "Employee" class object
		 */

		Employee emp = new Employee();
                try{
                String idNum = employeeID.replaceAll("-", "");
                System.out.println(employeeID + "is empid");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * \n" +
                    "FROM Employee E, Person P, Location L\n" +
                    "WHERE E.SSN = P.SSN AND L.ZipCode = P.ZipCode AND E.ID = " + employeeID);
                    rs.next();
                    emp.setEmployeeID(rs.getString("Id"));
                    emp.setStartDate(rs.getString("StartDate"));
                    emp.setHourlyRate(rs.getInt("HourlyRate"));

                    emp.setFirstName(rs.getString("FirstName"));
                    emp.setLastName(rs.getString("LastName"));
                    emp.setAddress(rs.getString("Address"));
                    emp.setId(rs.getString("Id"));
                    emp.setEmail(rs.getString("Email"));

                    Location loc = new Location();
                    loc.setCity(rs.getString("City"));
                    loc.setZipCode(rs.getInt("ZipCode"));
                    loc.setState(rs.getString("State"));

                    emp.setLocation(loc);
                    emp.setSsn(rs.getString("SSN"));
                    emp.setTelephone(rs.getString("Telephone"));

                       
  
                }
                catch(Exception e){
                    System.out.println(e);
                }

		
		return emp;

	}
	
	public Employee getHighestRevenueEmployee() {
		
		/*
		 * The students code to fetch employee data who generated the highest revenue will be written here
		 * The record is required to be encapsulated as a "Employee" class object
		 */
		
		Employee emp = new Employee();
                
                try{
                    System.out.println("Getting highest revenue employee.");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT DISTINCT R1.BrokerId, R1.Revenue\n" +
                    "FROM\n" +
                    "	((SELECT T.BrokerId, sum(TR.Fee) as Revenue\n" +
                    "	FROM Transaction TR, Trade T\n" +
                    "	WHERE TR.Id = T.TransactionId\n" +
                    "	GROUP BY T.BrokerId) as R1,\n" +
                    "    (SELECT T.BrokerId, sum(TR.Fee) as Revenue\n" +
                    "	FROM Transaction TR, Trade T\n" +
                    "	WHERE TR.Id = T.TransactionId\n" +
                    "	GROUP BY T.BrokerId) as R2\n" +
                    "    )\n" +
                    "WHERE R1.Revenue >= R2.Revenue;");
                    
                    //should only be one element
                    //now we use the getEmployee method
                    rs.next();
                    emp = getEmployee(rs.getString("BrokerId"));
                }
                catch(Exception e){
                    System.out.println(e);
                }
                
		return emp;

	}

	public String getEmployeeID(String username) {
		/*
		 * The students code to fetch data from the database based on "username" will be written here
		 * username, which is the Employee's email address who's Employee ID has to be fetched, is given as method parameter
		 * The Employee ID is required to be returned as a String
		 */

		String res = "";
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT SSN\n" +
                    "FROM Person\n" +
                    "WHERE Email = '"+username+"'");
                    
                    rs.next();
                    res = rs.getString("SSN");
                   
                }
                catch(Exception e){
                    System.out.println(e);
                }
                if(res.equals("")) {
                    System.out.println("Using default employeeid");
                    return "111-11-1111";
                }
                return res;
	}
}
