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

import model.Stock;

public class StockDao {

    public Stock getDummyStock() {
        Stock stock = new Stock();
        stock.setName("Apple");
        stock.setSymbol("AAPL");
        stock.setPrice(150.0);
        stock.setNumShares(1200);
        stock.setType("Technology");

        return stock;
    }

    public List<Stock> getDummyStocks() {
        List<Stock> stocks = new ArrayList<Stock>();

		/*Sample data begins*/
        for (int i = 0; i < 10; i++) {
            stocks.add(getDummyStock());
        }
		/*Sample data ends*/

        return stocks;
    }

    public List<Stock> getActivelyTradedStocks() {
	List<Stock> stocks = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT S.StockSymbol, S.Type, S.CompanyName, S.PricePerShare, SUM(O.NumShares) as Frequency\n" +
            "FROM Trade T, Orders O, Stock S\n" +
            "WHERE T.OrderId = O.Id AND T.StockId = S.StockSymbol \n" +
            "GROUP BY S.StockSymbol\n" +
            "ORDER BY Frequency DESC\n" +
            "LIMIT 0, 10;");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setNumShares(rs.getInt("Frequency"));
                stock.setType(rs.getString("Type"));
                stock.setName(rs.getString("CompanyName"));
                stock.setPrice(rs.getInt("PricePerShare"));
                stocks.add(stock);
                
            }
        } catch(Exception e) { }            
        
        return stocks;

    }

	public List<Stock> getAllStocks() {
		
		/*
		 * The students code to fetch data from the database will be written here
		 * Return list of stocks
		 */
        List<Stock> stocks = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("Select CompanyName, StockSymbol, Type, NumShares, PricePerShare\n" +
                                            "From Stock");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setName(rs.getString("CompanyName"));
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setType(rs.getString("Type"));
                stock.setNumShares(rs.getInt("NumShares"));
                stock.setPrice(rs.getInt("PricePerShare"));
                
                stocks.add(stock);
  
            }
        } catch(Exception e) {
        System.out.println(e);
        }            

        return stocks;

	}

    public Stock getStockBySymbol(String stockSymbol)
    {
        Stock stock = new Stock();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("Select S.CompanyName, S.StockSymbol, S.Type, S.NumShares, S.PricePerShare\n" +
                                            "From Stock S\n" +
                                            "Where S.StockSymbol = '" + stockSymbol + "'");
            while(rs.next()) {
                stock.setName(rs.getString("CompanyName"));
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setType(rs.getString("Type"));
                stock.setNumShares(rs.getInt("NumShares"));
                stock.setPrice(rs.getInt("PricePerShare"));
  
            }
        } catch(Exception e) {
        System.out.println(e);
        }            

        return stock;
    }

    public String setStockPrice(String stockSymbol, double stockPrice) {
        /*
         * The students code to fetch data from the database will be written here
         * Perform price update of the stock symbol
         */
        if(stockPrice <= 0) {
            return "failure";
        }
        int oldPrice = 0;
        try {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            //Get old price
            ResultSet ps = st.executeQuery("Select PricePerShare\n" +
                            "From Stock\n" +
                            "Where StockSymbol = '" + stockSymbol + "'");
            ps.next();
            oldPrice = ps.getInt("PricePerShare");
            System.out.println("Old price is " + oldPrice);
            st.executeUpdate("UPDATE Stock\n" +
                                           "SET PricePerShare = " + stockPrice + "\n" +
                                           "WHERE StockSymbol = '" + stockSymbol + "'");
            st.executeUpdate("INSERT INTO PriceChange\n" +
                             "VALUES('" + stockSymbol + "', " + stockPrice + ", CONVERT('" + dateFormat.format(date) + "', DATETIME))");
        } catch(Exception e) { }
        
        /*
        Basic functionality done.
        Now any hidden and trailing stops should be considered and transactions created as appopriate.
        */
        
        //Hidden stop (has fixed price to sell at):
            //Get all trades with hidden stops that haven't sold
            //Trace back to AccountId and check if HS.NumStocks > O.NumShares
            //Create a transaction with
                //Id: Next TransactionId
                //Fee: stockPrice*O.NumShares (NOT PricePerShare)
                //DateTime: Now
                //PricePerShare: stockPrice (NOT PricePerShare)
            //Add that transaction to the trade object
            //Decrement HS.NumShares by O.NumShares
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
        Statement st = con.createStatement();
        ResultSet hs = st.executeQuery("Select H.NumShares as TotalShares, HiddenStops.NumShares as SellShares, HiddenStops.PricePerShare, HiddenStops.OrderId, HiddenStops.AccountId\n" +
        "From HasStock H, \n" +
        "(Select AccountId, NumShares, PricePerShare, OrderId\n" +
        "From Orders O, Trade T\n" +
        "Where O.Id = T.OrderId AND T.TransactionId IS NULL AND O.Price = 'HiddenStop' AND T.StockId = '" + stockSymbol + "') as HiddenStops\n" +
        "Where H.AccountId = HiddenStops.AccountId and H.StockSymbol = '" + stockSymbol + "'");
        
        while(hs.next()) {
            int sellShares = hs.getInt("SellShares");
            int totalShares = hs.getInt("TotalShares");
            int pricePerShare = hs.getInt("PricePerShare");
            int orderId = hs.getInt("OrderId");
            int accountId = hs.getInt("AccountId");
            
            //Ensure that there are enough shares to sell AND price fell below minimum
            if(sellShares <= totalShares && pricePerShare >= stockPrice) {
                //Sell this stock, starting by creating transaction
                Statement st2 = con.createStatement();
                ResultSet maxTransId = st2.executeQuery("Select MAX(Id) as maxId\n" +
                                       "From Transaction");
                int nextTransId = 1;
                if(maxTransId.next()) {
                    nextTransId = maxTransId.getInt("maxId") + 1;
                }
                int fee = (int)(stockPrice * sellShares*0.05); //current price*amount selling
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                //Insert the transaction
                st2.executeUpdate("INSERT INTO TRANSACTION\n" +
                        "Values(" + nextTransId + ", " + fee + ", CONVERT('" + dateFormat.format(date) + "', DATETIME), " + stockPrice + ")");
                //Insert the trade
                st2.executeUpdate("Update Trade\n" +
                        "Set TransactionId = " + nextTransId + "\n" +
                        "Where OrderId = " + orderId);
                //Update HasStock shares
                st2.executeUpdate("Update HasStock\n" +
                        "Set NumShares = " + (totalShares - sellShares) + "\n" +
                        "Where AccountID = " + accountId + " AND StockSymbol = '" + stockSymbol + "'");
                System.out.println("Successfully inserted/updated everything");
            }
            
        }
        } catch(Exception e) {
            System.out.println(e);
            System.out.println("Failure to insert transaction, trade, and/or stock update.");
        }
        
        //Trailing stop (has percentage of original bought price to sell at - this is more complicated):
        //Get all trades with trailing stops that haven't sold
        //If stockPrice > oldPrice:
            //Update PricePerShare in Order 
        //Else:
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet hs = st.executeQuery("Select H.NumShares as TotalShares, HiddenStops.NumShares as SellShares, HiddenStops.PricePerShare, HiddenStops.OrderId, HiddenStops.AccountId, HiddenStops.Percentage\n" +
            "From HasStock H, \n" +
            "(Select AccountId, NumShares, PricePerShare, OrderId, Percentage\n" +
            "From Orders O, Trade T\n" +
            "Where O.Id = T.OrderId AND T.TransactionId IS NULL AND O.Price = 'TrailingStop' AND T.StockId = '" + stockSymbol + "') as HiddenStops\n" +
            "Where H.AccountId = HiddenStops.AccountId and H.StockSymbol = '" + stockSymbol + "'");
            
            while(hs.next()) {
            int sellShares = hs.getInt("SellShares");
            int totalShares = hs.getInt("TotalShares");
            int pricePerShare = hs.getInt("PricePerShare");
            int orderId = hs.getInt("OrderId");
            int accountId = hs.getInt("AccountId");
            double percentage = hs.getInt("Percentage");
            
                if(stockPrice > oldPrice) {
                    int newPrice = (int)(stockPrice - ((percentage/100)*stockPrice));
                    
                    Statement st2 = con.createStatement();
                    st2.executeUpdate("Update Orders\n" +
                            "Set PricePerShare = " + newPrice + "\n" +
                            "Where Id = " + orderId);
                
                } else if(stockPrice < oldPrice) {
                    //Ensure that there are enough shares to sell AND price fell below minimum
                    if(sellShares <= totalShares && pricePerShare >= stockPrice) {
                        //Sell this stock, starting by creating transaction
                        Statement st2 = con.createStatement();
                        ResultSet maxTransId = st2.executeQuery("Select MAX(Id) as maxId\n" +
                                               "From Transaction");
                        int nextTransId = 1;
                        if(maxTransId.next()) {
                            nextTransId = maxTransId.getInt("maxId") + 1;
                        }
                        int fee = (int)(stockPrice * sellShares*0.05); //current price*amount selling
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        //Insert the transaction
                        st2.executeUpdate("INSERT INTO TRANSACTION\n" +
                                "Values(" + nextTransId + ", " + fee + ", CONVERT('" + dateFormat.format(date) + "', DATETIME), " + stockPrice + ")");
                        //Insert the trade
                        st2.executeUpdate("Update Trade\n" +
                                "Set TransactionId = " + nextTransId + "\n" +
                                "Where OrderId = " + orderId);
                        //Update HasStock shares
                        st2.executeUpdate("Update HasStock\n" +
                                "Set NumShares = " + (totalShares - sellShares) + "\n" +
                                "Where AccountID = " + accountId + " AND StockSymbol = '" + stockSymbol + "'");
                        System.out.println("Successfully inserted/updated everything");
                        }
                        }
            }
        } catch(Exception e) {
            System.out.println(e);
        }
        
        return "success";
    }
	
	public List<Stock> getOverallBestsellers() {

		/*
		 * The students code to fetch data from the database will be written here
		 * Get list of bestseller stocks
		 */
                
        List<Stock> stocks = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT S.StockSymbol, S.CompanyName, S.Type, S.PricePerShare, SUM(O.NumShares) as Frequency\n" +
                        "FROM Trade T, Orders O, Stock S\n" +
                        "WHERE T.OrderId = O.Id AND T.StockId = S.StockSymbol AND O.OrderType = 'Buy'\n" +
                        "GROUP BY S.StockSymbol\n" +
                        "ORDER BY Frequency DESC\n" +
                        "LIMIT 0, 10;");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setName(rs.getString("CompanyName"));
                stock.setType(rs.getString("Type"));
                stock.setNumShares(rs.getInt("Frequency"));
                stock.setPrice(rs.getInt("PricePerShare"));
                stocks.add(stock);
                
            }
        } catch(Exception e) { }
                
        return stocks;

	}

    public List<Stock> getCustomerBestsellers(String customerID) {

		/*
		 * The students code to fetch data from the database will be written here.
		 * Get list of customer bestseller stocks
		 */

        int idNumber = Integer.parseInt(customerID.replaceAll("-", "")); //database stores customerId as INTEGER w/o hyphens
        List<Stock> stocks = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT S.StockSymbol, S.Type, S.CompanyName, S.NumShares, S.PricePerShare, SUM(O.NumShares) as Frequency\n" +
            "FROM Trade T, Orders O, Account A, Stock S\n" +
            "WHERE T.OrderId = O.Id AND T.StockId = S.StockSymbol AND T.AccountId = A.Id\n" +
            "AND A.Client = " + idNumber + " AND O.OrderType = 'Buy'\n" +
            "GROUP BY S.StockSymbol\n" +
            "ORDER BY Frequency DESC\n" +
            "LIMIT 0, 10;");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setType(rs.getString("Type"));
                stock.setName(rs.getString("CompanyName"));
                stock.setNumShares(rs.getInt("NumShares"));
                stock.setPrice(rs.getInt("PricePerShare"));
                stocks.add(stock);
                
            }
        } catch(Exception e) { }            
        
        return stocks;

    }
        //HasStock should include the price to keep track of the same stock bought at different prices
	public List getStocksByCustomer(String customerId) {

		/*
		 * The students code to fetch data from the database will be written here
		 * Get stockHoldings of customer with customerId
		 */
        int idNumber = Integer.parseInt(customerId.replaceAll("-", "")); //database stores customerId as INTEGER w/o hyphens
        List<Stock> stocks = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT H.StockSymbol, S.Type, S.CompanyName, S.PricePerShare, H.NumShares\n" +
            "FROM HasStock H, Account A, Stock S\n" +
            "WHERE A.Client = " + idNumber + " AND H.AccountId = A.Id AND H.StockSymbol = S.StockSymbol;");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setType(rs.getString("Type"));
                stock.setName(rs.getString("CompanyName"));
                stock.setNumShares(rs.getInt("NumShares"));
                stock.setPrice(rs.getInt("PricePerShare"));
                stocks.add(stock);
                
            }
        } catch(Exception e) { }            
        
        return stocks;
	}

    public List<Stock> getStocksByName(String name) {

		/*
		 * The students code to fetch data from the database will be written here
		 * Return list of stocks matching "name"
		 */

        List<Stock> stocks = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT S.StockSymbol, S.Type, S.CompanyName, S.NumShares, S.PricePerShare\n" +
            "FROM Stock S, Trade T, Orders O\n" +
            "WHERE S.CompanyName like \'%" + name + "%\' AND S.StockSymbol = T.StockId AND T.OrderId = O.Id\n" +
            "GROUP BY S.StockSymbol\n" +
            "ORDER BY O.DateTime DESC\n" +
            "LIMIT 0, 10;");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setType(rs.getString("Type"));
                stock.setName(rs.getString("CompanyName"));
                stock.setNumShares(rs.getInt("NumShares"));
                stock.setPrice(rs.getInt("PricePerShare"));
                stocks.add(stock);
                
            }
        } catch(Exception e) { }            
        
        return stocks;
    }

    public List<Stock> getStockSuggestions(String customerID) {

		/*
		 * The students code to fetch data from the database will be written here
		 * Return stock suggestions for given "customerId"
		 */
        int idNumber = Integer.parseInt(customerID.replaceAll("-", "")); //database stores customerId as INTEGER w/o hyphens
        List<Stock> stocks = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT S.StockSymbol, S.CompanyName, S.Type, S.NumShares, S.PricePerShare\n" +
            "FROM Stock S\n" +
            "WHERE S.Type IN\n" + //Include stocks of the same type as ones customer has previously traded
            "(SELECT S2.Type\n" +
            "FROM Account A, Trade T, Stock S2\n" +
            "WHERE A.Client = " + idNumber + " AND A.Id = T.AccountId AND T.StockId = S2.StockSymbol)\n" +
            "AND S.StockSymbol NOT IN\n" + //Don't include stocks customer currently owns
            "(SELECT H.StockSymbol\n" +
            "FROM HasStock H, Account A\n" +
            "WHERE A.Client = " + idNumber + " AND A.Id = H.AccountID);");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setType(rs.getString("Type"));
                stock.setName(rs.getString("CompanyName"));
                stock.setNumShares(rs.getInt("NumShares"));
                stock.setPrice(rs.getInt("PricePerShare"));
                stocks.add(stock);
                
            }
        } catch(Exception e) { }            
        
        return stocks;

    }

    public List<Stock> getStockPriceHistory(String stockSymbol) {

		/*
		 * The students code to fetch data from the database
		 * Return list of stock objects, showing price history
		 */
        List<Stock> stocks = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT PC.StockId, S.Type, S.CompanyName, S.NumShares, PC.StockPrice\n" +
            "FROM PriceChange PC, Stock S\n" +
            "WHERE PC.StockId = '" + stockSymbol + "' AND PC.StockId = S.StockSymbol\n" +
            "ORDER BY PC.DateTime");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setSymbol(rs.getString("StockId"));
                stock.setType(rs.getString("Type"));
                stock.setName(rs.getString("CompanyName"));
                stock.setNumShares(rs.getInt("NumShares"));
                stock.setPrice(rs.getInt("StockPrice"));
                stocks.add(stock);
                
            }
        } catch(Exception e) { }            
        
        return stocks;
       
    }

    public List<String> getStockTypes() {

		/*
		 * The students code to fetch data from the database will be written here.
		 * Populate types with stock types
		 */

        List<String> types = new ArrayList<String>();
        types.add("Automotive");
        types.add("Computer");
        return types;

    }

    public List<Stock> getStockByType(String stockType) {

		/*
		 * The students code to fetch data from the database will be written here
		 * Return list of ystocks of type "stockType"
		 */

        List<Stock> stocks = new ArrayList<Stock>();
        try {
            System.out.println(stockType);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql4.cs.stonybrook.edu:3306/bweinger", "bweinger", "111639717");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("Select S.StockSymbol, S.Type, S.CompanyName, S.NumShares, S.PricePerShare\n" +
                                           "From Stock S\n" +
                                           "Where S.Type = '" + stockType + "'");
            while(rs.next()) {
                Stock stock = new Stock();
                stock.setSymbol(rs.getString("StockSymbol"));
                stock.setType(rs.getString("Type"));
                stock.setName(rs.getString("CompanyName"));
                stock.setNumShares(rs.getInt("NumShares"));
                stock.setPrice(rs.getInt("PricePerShare"));
                stocks.add(stock);
                
            }
        } catch(Exception e) { }            
        
        return stocks;
    }
}
