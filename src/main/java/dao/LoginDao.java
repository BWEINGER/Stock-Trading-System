package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import model.Login;

public class LoginDao {
	/*
	 * This class handles all the database operations related to login functionality
	 */
	
	
	public Login login(String username, String password, String role) {
		/*
		 * Return a Login object with role as "manager", "customerRepresentative" or "customer" if successful login
		 * Else, return null
		 * The role depends on the type of the user, which has to be handled in the database
		 * username, which is the email address of the user, is given as method parameter
		 * password, which is the password of the user, is given as method parameter
		 * Query to verify the username and password and fetch the role of the user, must be implemented
		 */
		
		/*Sample data begins*/
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("Select *\n" +
                            "From Login\n" +
                            "Where Username = '" + username + "' AND Password = '" + password + "' AND Role = '" + role + "';");
                    if(!rs.next()) {
                        System.out.println("Empty set"); //RETURN NULL AFTER TESTING
                        return null;
                    }
                } catch(Exception e) {
                    System.out.println(e);
                }
		Login login = new Login();
		login.setRole(role);
		return login;
		/*Sample data ends*/
		
	}
	
	public String addUser(Login login) {
		/*
		 * Query to insert a new record for user login must be implemented
		 * login, which is the "Login" Class object containing username and password for the new user, is given as method parameter
		 * The username and password from login can get accessed using getter methods in the "Login" model
		 * e.g. getUsername() method will return the username encapsulated in login object
		 * Return "success" on successful insertion of a new user
		 * Return "failure" for an unsuccessful database operation
		 */
                try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                Statement st = con.createStatement();
                st.executeUpdate("Insert into Login\n" +
                "Values('" + login.getUsername() + "', '" + login.getPassword() + "', '" + login.getRole() + "')");
                } catch(Exception e) {
                    System.out.println(e);
                }
		/*Sample data begins*/
		return "success";
		/*Sample data ends*/
	}

}
