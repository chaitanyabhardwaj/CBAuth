package cbauth;

import cbauth.exception.InvalidURLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DatabaseManager {
    
    private Connection connection;
    final private String URL, USERNAME, PASSWORD;
    final public String DB_NAME;
    
    final private static Pattern 
        URL_PATTERN_1 = Pattern.compile("//\\w+:\\d+/\\w+/"),
        URL_PATTERN_2 = Pattern.compile("//\\w+:\\d+/\\w+"),
        URL_PATTERN_3 = Pattern.compile("\\w+:\\d+/\\w+"),
        URL_PATTERN_4 = Pattern.compile("\\w+:\\d+/\\w+/");
    
    private DatabaseManager(String databaseURL, String dbname, String username, String password) {
        URL = databaseURL;
        DB_NAME = dbname;
        USERNAME = username;
        PASSWORD = password;
    }
    
    public void closeConnection()throws SQLException {
        //IF CONNECTION IS NOT CLOSED, THEN CLOSE THE CONNECTION
        if(!connection.isClosed())
            connection.close();
    }
    
    public void createDatabase()throws SQLException {
        //LOADING DRIVER UNNECESSARY SINCE V8.0.13
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException ex) {
            System.out.println(ex.toString());
        }
        //default mysql database
        String databaseURL = "jdbc:mysql://localhost:3306/mysql";
        System.out.println(DB_NAME);
        Connection c = DriverManager.getConnection(databaseURL, USERNAME, PASSWORD);
        Statement s = c.createStatement();
        s.executeUpdate("CREATE DATABASE " + DB_NAME + ";");
        c.close();
    }
    
    public boolean databaseExists()throws SQLException {
        try {
            List<String> list = listDatabases(USERNAME, PASSWORD);
            return list.contains(DB_NAME);
        }
        catch(InvalidURLException ex) {
            System.out.println(ex.toString());
        }
        return false;
    }
    
    public ResultSet executeQuery(String query) throws SQLException {
        Statement s = connection.createStatement();
        return s.executeQuery(query);
    }
    
    public int executeUpdate(String query) throws SQLException {
        Statement s = connection.createStatement();
        return s.executeUpdate(query);
    }
    
    public static DatabaseManager getInstance(String databaseURL, String username, String password)throws InvalidURLException {
        String dbname;
        if(!isValidURL(databaseURL))
            throw new InvalidURLException("The database URL is invalid");
        databaseURL = databaseURL.trim();
        if(databaseURL.charAt(databaseURL.length() - 1) == '/')
            databaseURL = databaseURL.substring(0, databaseURL.length() - 1);
        databaseURL = "jdbc:mysql:" + databaseURL;
        dbname = databaseURL.substring(databaseURL.lastIndexOf('/') + 1);
        DatabaseManager manager = new DatabaseManager(databaseURL, dbname, username, password);
        return manager;
    }
    
    public static boolean isValidURL(String url) {
        return (URL_PATTERN_1.matcher(url).matches() || URL_PATTERN_2.matcher(url).matches() || URL_PATTERN_3.matcher(url).matches() || URL_PATTERN_4.matcher(url).matches());
    }
    
    public static List<String> listDatabases(String username, String password)throws SQLException, InvalidURLException {
        //LOADING DRIVER UNNECESSARY SINCE V8.0.13
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException ex) {
            System.out.println(ex.toString());
        }
        List<String> list = new ArrayList<>();
        //default mysql database
        String databaseURL = "jdbc:mysql://localhost:3306/mysql";
        System.out.println(databaseURL);
        Connection c = DriverManager.getConnection(databaseURL, username, password);
        ResultSet set = c.getMetaData().getCatalogs();
        while(set.next())
            list.add(set.getString(1));
        c.close();
        return list;
    }
    
    public List<String> listTables()throws SQLException {
        List<String> list = new ArrayList<>();
        Statement s = connection.createStatement();
        ResultSet set = s.executeQuery("SHOW TABLES;");
        while(set.next())
            list.add(set.getString(1));
        return list;
    }
    
    public void openConnection()throws SQLException {
        //LOADING DRIVER UNNECESSARY SINCE V8.0.13
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException ex) {
            System.out.println(ex.toString());
        }
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    
    public boolean tableExists(String tableName)throws SQLException {
        List<String> list = listTables();
        return list.contains(tableName);
    }
    
}