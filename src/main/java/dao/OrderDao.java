package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDao {

    public Order getDummyTrailingStopOrder() {
        TrailingStopOrder order = new TrailingStopOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setPercentage(12.0);
        return order;
    }

    public Order getDummyMarketOrder() {
        MarketOrder order = new MarketOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setBuySellType("buy");
        return order;
    }

    public Order getDummyMarketOnCloseOrder() {
        MarketOnCloseOrder order = new MarketOnCloseOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setBuySellType("buy");
        return order;
    }

    public Order getDummyHiddenStopOrder() {
        HiddenStopOrder order = new HiddenStopOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setPricePerShare(145.0);
        return order;
    }

    public List<Order> getDummyOrders() {
        List<Order> orders = new ArrayList<Order>();

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyTrailingStopOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyMarketOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyMarketOnCloseOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyHiddenStopOrder());
        }

        return orders;
    }

    public String submitOrder(Order order, Customer customer, Employee employee, Stock stock) {

		/*
		 * Student code to place stock order
		 * Employee can be null, when the order is placed directly by Customer
         * */
                String percentage = "NULL";
                String price = "NULL";
                String pricePerShare = "NULL";
                String orderType = "NULL";
             if( order instanceof MarketOrder){
                 price = "Market";
                 orderType = ((MarketOrder) order).getBuySellType();
             }   
             
             else if( order instanceof TrailingStopOrder){
                orderType = "Sell";
                price = "TrailingStop";
                int percentageNum = (int)(((TrailingStopOrder) order).getPercentage());
                percentage = Double.toString(percentageNum);
                //pricePerShare = Double.toString((stock.getPrice() - ((TrailingStopOrder)order).getPercentage()/100)*(stock.getPrice()));
                pricePerShare = Double.toString(stock.getPrice() - (stock.getPrice()*percentageNum/100));
             }  
             
             else if( order instanceof HiddenStopOrder){
                orderType="Sell";
                price = "HiddenStop";
                pricePerShare = Double.toString( ((HiddenStopOrder) order).getPricePerShare() );
             }
             
             else if ( order instanceof MarketOnCloseOrder){
                price = "MarketOnClose";
                orderType = ((MarketOnCloseOrder) order).getBuySellType();
             }
                
                
            int numShares = order.getNumShares();
            Date dateTime = order.getDatetime();
            order.incrementId();
            int id = order.getId();
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
            
            // Attributes for trade
            int accId = -1;             // Where do we get account id
            
            
            try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    
                    String date = (dateFormat.format(dateTime)).toString();
                    
                    //Get order id
                    ResultSet maxId = st.executeQuery("Select MAX(Id) as IdNum\n" +
                            "From Orders");
                    if(maxId.next()) {
                        id = maxId.getInt("IdNum") + 1;
                    } else {
                        id = 1;
                    }
                    
                    //Insert into orders
                    st.executeUpdate("INSERT INTO Orders\n" +
                                     "VALUES("+numShares+", "+ pricePerShare+", "+id+", '"+date+"', "+percentage+", '"+price+"', '"+orderType+"');");  //works for buy and sell, issue trailing stop and hidden stop
                            //"VALUES(50, 100, 10, '"+date+"', 10, 'Market', 'Buy');");

                    
                    if(!price.equals("TrailingStop") && !price.equals("HiddenStop")){
                        
                    //Insert into trades
                        if(orderType.equals("Buy")){
                            System.out.println("order1");
                            ResultSet getSharesOwned = st.executeQuery(
                                    "SELECT *\n"+
                                    "FROM HasStock HS\n"+
                                    "WHERE HS.StockSymbol='"+stock.getSymbol()+"' AND HS.AccountId="+customer.getAccountNumber()+";"
                                    );
                            
                            int numberOwned = -1;
                            
                            
                            if(getSharesOwned.next()==true){
                                System.out.println("order2");
                                numberOwned = getSharesOwned.getInt("NumShares");
                                numberOwned = numberOwned + order.getNumShares();
                                
                                System.out.println(stock.getSymbol());
                                st.executeUpdate(
                                        "UPDATE HasStock\n"+
                                        "SET NumShares = NumShares +"+order.getNumShares()+"\n"+
                                        "WHERE StockSymbol='"+stock.getSymbol()+"' AND AccountId="+customer.getAccountNumber()+";"
                                    );
                                      
                            }
                            
                            if(numberOwned == -1){
                                numberOwned = order.getNumShares();
                                System.out.println("doesnt own");
                                st.executeUpdate(
                                        "INSERT INTO HasStock\n"+
                                        "VALUES ("+customer.getAccountNumber()+",'"+stock.getSymbol()+"',"+order.getNumShares()+");"
                                );
                            }
                        }
                        else if(orderType.equals("Sell")){
                            ResultSet getSharesOwned = st.executeQuery(
                                    "SELECT *\n"+
                                    "FROM HasStock HS\n"+
                                    "WHERE HS.StockSymbol='"+stock.getSymbol()+"' AND HS.AccountId="+customer.getAccountNumber()+";"
                                    );
                            
                            int numberOwned = -1;
                            if(getSharesOwned.next()==true){
                                System.out.println("order2");
                                numberOwned = getSharesOwned.getInt("NumShares");       /// Selling more shares than we own
                                numberOwned = numberOwned - order.getNumShares();         // 
                                if(numberOwned <0){
                                    return "failure";
                                }
                                
                                System.out.println(stock.getSymbol());
                                st.executeUpdate(
                                        "UPDATE HasStock\n"+
                                        "SET NumShares = NumShares -"+order.getNumShares()+"\n"+
                                        "WHERE StockSymbol='"+stock.getSymbol()+"' AND AccountId="+customer.getAccountNumber()+";"
                                    );
                                      
                            }
                            else{
                                return "failure";
                            }
                        }
                            
                        int fee = (int) (((double) order.getNumShares() * stock.getPrice()) * 0.05);
                        System.out.println(fee);
                        // Insert into transactions 
                        ResultSet maxTransId = st.executeQuery("Select MAX(Id) as maxId\n" +
                            "From Transaction");
                        int nextTransId = 1;
                        if(maxTransId.next()) {
                            nextTransId = maxTransId.getInt("maxId") + 1;
                        }
                        st.executeUpdate(
                                "INSERT INTO Transaction\n"+
                                "VALUES ("+nextTransId+", "+fee+",'"+date+"', "+stock.getPrice()+");"  
                                        );


                        //Insert into trade table - includes AccountId,BrokerId,TransactionId,OrderId,StockId
                        // Need to get the transactionId from the transaction
                        System.out.println("insert into trade");
                        System.out.println("What is employee " + employee);
                        String employeeId;
                        if(employee == null){
                            employeeId="NULL";
                            //employeeId = "1";
                        }
                        else{
                            employeeId = employee.getEmployeeID();
                        }

                        st.executeUpdate(
                                "INSERT INTO Trade\n"+
                                "VALUES("+customer.getAccountNumber()+","+employeeId+", "+nextTransId+", "+
                                id+", '"+stock.getSymbol()+"');" );
                        System.out.println("done inserting trade");
                        order.incrementTransId();
                }
                    else{
                        //Trailing or hidden stop. First get employeeId
                        String employeeId;
                        if(employee == null){
                            employeeId="NULL";
                            //employeeId = "1";
                        }
                        else{
                            employeeId = employee.getEmployeeID();
                        }
                        //Make a trade object with NULL transaction. It will be created on sell.
                        st.executeUpdate(
                                "INSERT INTO Trade\n"+
                                "VALUES("+customer.getAccountNumber()+","+employeeId+", NULL, "+
                                id+", '"+stock.getSymbol()+"');" );
                    }
            }
                catch(Exception e){
                    System.out.println(e);
                }
		/*Sample data begins*/
        return "success";
		/*Sample data ends*/

    }

    public List<Order> getOrderByStockSymbol(String stockSymbol) {          // WORKS
        /*
		 * Student code to get orders by stock symbol
         */
        List<Order> orders = new ArrayList<Order>();
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT O.Id, S.StockSymbol, O.NumShares,O.PricePerShare, O.Percentage, O.DateTime, O.Price, O.OrderType\n"+
                            "FROM Trade T, Orders O, Stock S\n"+
                            "WHERE S.StockSymbol = '"+stockSymbol+"' AND S.StockSymbol = T.StockId AND T.OrderID = O.Id;");
                    
                    while(rs.next()){
                        String type = rs.getString("Price");
                        if(type.equals("Market")) {
                            MarketOrder order = new MarketOrder();
                            order.setDatetime(rs.getDate("DateTime"));
                            order.setId(rs.getInt("Id"));
                            order.setNumShares(rs.getInt("NumShares"));
                            order.setBuySellType(rs.getString("OrderType"));
                            orders.add(order);
                        } else if(type.equals("MarketOnClose")) {
                            MarketOrder order = new MarketOrder();
                            order.setDatetime(rs.getDate("DateTime"));
                            order.setId(rs.getInt("Id"));
                            order.setNumShares(rs.getInt("NumShares"));
                            order.setBuySellType(rs.getString("OrderType"));
                            orders.add(order);
                        } else if(type.equals("TrailingStop")) {
                            TrailingStopOrder order = new TrailingStopOrder();
                            order.setDatetime(rs.getDate("DateTime"));
                            order.setId(rs.getInt("Id"));
                            order.setNumShares(rs.getInt("NumShares"));
                            order.setPercentage(rs.getInt("Percentage"));
                            orders.add(order);
                        } else if(type.equals("HiddenStop")) {
                            HiddenStopOrder order = new HiddenStopOrder();
                            order.setDatetime(rs.getDate("DateTime"));
                            order.setId(rs.getInt("Id"));
                            order.setNumShares(rs.getInt("NumShares"));
                            order.setPricePerShare(rs.getInt("PricePerShare"));
                            orders.add(order);
                        }
                    }
                }
                catch(Exception e){
                    System.out.println(e);
                }
        return orders;
        //return getDummyOrders();
    }

      public List<Order> getOrderByCustomerName(String customerName) {        // Does not yet work
         /*
		 * Student code to get orders by customer name
         */
         customerName = customerName+" ";
         int numNameParts = 0;
         String nameParts[] = customerName.split(" ");
         for (String part : nameParts){
             numNameParts++;
         }
         
         String firstName = "";
         String lastName = "";
         if(numNameParts>=2){
            firstName = nameParts[0];
            lastName = nameParts[1];
         }
         else{
             firstName = "";  
             lastName = "";
         }
             

         System.out.println(firstName + "11111111111111");
         System.out.println(lastName + "222222222222");
         
        List<Order> orders = new ArrayList<Order>();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT O.Id, S.StockSymbol, O.NumShares, O.PricePerShare, O.Percentage, O.DateTime,  O.Price, O.OrderType, P.FirstName, P.LastName, C.Id\n"+
                    "FROM Trade T, Orders O, Account A, Person P, Client C, Stock S\n"+
                    "WHERE P.FirstName = '"+firstName+"' AND P.LastName = '"+lastName+"'  AND P.SSN = A.Client AND A.Id = T.AccountId AND T.OrderId = O.Id AND A.Client = C.Id AND T.StockId = S.StockSymbol;");
            while(rs.next()){
                String type = rs.getString("Price");
                        if(type.equals("Market")) {
                            MarketOrder order = new MarketOrder();
                            order.setDatetime(rs.getDate("DateTime"));
                            order.setId(rs.getInt("Id"));
                            order.setNumShares(rs.getInt("NumShares"));
                            order.setBuySellType(rs.getString("OrderType"));
                            orders.add(order);
                        } else if(type.equals("MarketOnClose")) {
                            MarketOrder order = new MarketOrder();
                            order.setDatetime(rs.getDate("DateTime"));
                            order.setId(rs.getInt("Id"));
                            order.setNumShares(rs.getInt("NumShares"));
                            order.setBuySellType(rs.getString("OrderType"));
                            orders.add(order);
                        } else if(type.equals("TrailingStop")) {
                            TrailingStopOrder order = new TrailingStopOrder();
                            order.setDatetime(rs.getDate("DateTime"));
                            order.setId(rs.getInt("Id"));
                            order.setNumShares(rs.getInt("NumShares"));
                            order.setPercentage(rs.getInt("Percentage"));
                            orders.add(order);
                        } else if(type.equals("HiddenStop")) {
                            HiddenStopOrder order = new HiddenStopOrder();
                            order.setDatetime(rs.getDate("DateTime"));
                            order.setId(rs.getInt("Id"));
                            order.setNumShares(rs.getInt("NumShares"));
                            order.setPricePerShare(rs.getInt("PricePerShare"));
                            orders.add(order);
                        }
            }
            //should only be one element
            //now we use the getCustomer method
        }catch(Exception e){
            System.out.println(e);
        }
        return orders;
    }


    public List<Order> getOrderHistory(String customerId) {
        /*
		 * The students code to fetch data from the database will be written here
		 * Show orders for given customerId
		 */
        
        List<Order> orders = new ArrayList<Order>();
                 try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT O.Id as orderId, O.DateTime, O.NumShares, O.Price, O.OrderType, O.Percentage, O.PricePerShare\n"+
                                                    "FROM Orders O, Account A, Client C, Person P, Trade T\n"+
                                                    "WHERE  C.Id = '"+customerId+"' AND P.SSN = C.Id AND C.Id = A.Client AND A.Id = T.AccountId AND T.OrderId = O.Id;");
                while(rs.next()){
                    String orderType = rs.getString("Price");
                    
                    if(orderType.equals("Market")) {
                        MarketOrder order = new MarketOrder();
                        order.setId(rs.getInt("orderId"));
                        order.setDatetime(rs.getDate("DateTime"));
                        order.setNumShares(rs.getInt("NumShares"));
                        order.setBuySellType(rs.getString("OrderType"));
                        orders.add(order);
                    } else if(orderType.equals("MarketOnClose")) {
                        MarketOnCloseOrder order = new MarketOnCloseOrder();
                        order.setId(rs.getInt("orderId"));
                        order.setDatetime(rs.getDate("DateTime"));
                        order.setNumShares(rs.getInt("NumShares"));
                        order.setBuySellType(rs.getString("OrderType"));
                        orders.add(order);
                    } else if(orderType.equals("HiddenStop")) {
                        HiddenStopOrder order = new HiddenStopOrder();
                        order.setId(rs.getInt("orderId"));
                        order.setDatetime(rs.getDate("DateTime"));
                        order.setNumShares(rs.getInt("NumShares"));
                        order.setPricePerShare(rs.getInt("PricePerShare"));
                        orders.add(order);
                    } else if(orderType.equals("TrailingStop")) {
                       TrailingStopOrder order = new TrailingStopOrder();
                       order.setId(rs.getInt("orderId"));
                       order.setDatetime(rs.getDate("DateTime"));
                       order.setNumShares(rs.getInt("NumShares"));
                       order.setPercentage(rs.getInt("Percentage"));
                       orders.add(order);
                    }
                }    
                    
                 }catch(Exception e){
                     System.out.println("Failed");
                 }
                 
        return orders;
    }


    public List<OrderPriceEntry> getOrderPriceHistory(String orderId) {

        /*
		 * The students code to fetch data from the database will be written here
		 * Query to view price history of hidden stop order or trailing stop order
		 * Use setPrice to show hidden-stop price and trailing-stop price
		 */
        List<OrderPriceEntry> orderPriceHistory = new ArrayList<OrderPriceEntry>();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT O.PricePerShare, PC.StockPrice, PC.DateTime, S.StockSymbol, O.Id\n" +
            "FROM Orders O, Stock S, Trade T, PriceChange PC\n" +
            "WHERE O.Id = " + orderId + " AND O.Id = T.OrderId AND T.StockID = S.StockSymbol AND PC.StockId = S.StockSymbol AND PC.DateTime>O.DateTime\n" +
            "ORDER BY PC.DateTime;");
            
            while(rs.next()) {
                OrderPriceEntry order = new OrderPriceEntry();
                order.setOrderId(orderId);
                String dateString = rs.getString("DateTime");
                Date date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);  
                order.setDate(date);
                order.setPrice(rs.getInt("StockPrice"));
                order.setPricePerShare(rs.getInt("PricePerShare"));
                order.setStockSymbol(rs.getString("StockSymbol"));
                orderPriceHistory.add(order);
            }
        } catch(Exception e) {
            System.out.println(e);
        }
        
        return orderPriceHistory;
    }
}



