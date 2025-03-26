package edu.ucalgary.oop;

import java.sql.*;

public class DataBaseManager {
    private Connection dbConnect;
    private ResultSet results;
    String url = "jdbc:postgresql://localhost:5432/project";
    String username = "oop";
    String password = "ucalgary";

    public void createConnection(){
        try{
            dbConnect = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } 
    }
    
}