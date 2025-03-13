package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Test for the DatabaseManager class
 */
public class DatabaseManagerTest {
    private DatabaseManager dbManager;
    
    @Before
    public void setUp() {
        dbManager = new DatabaseManager("jdbc:sqlite:data/disaster_management.db");
    }
    
    @Test
    public void testConnection() {
        assertTrue("Database connection should be established", dbManager.isConnected());
    }
    
    @Test
    public void testLoadVictims() {
        try {
            ArrayList<DisasterVictim> victims = dbManager.loadVictims();
            assertNotNull("Victims list should not be null", victims);
        } catch (SQLException e) {
            fail("Exception thrown when loading victims: " + e.getMessage());
        }
    }
    
    
    @Test
    public void testLoadLocations() {
        try {
            ArrayList<Location> locations = dbManager.loadLocations();
            assertNotNull("Locations list should not be null", locations);
        } catch (SQLException e) {
            fail("Exception thrown when loading locations: " + e.getMessage());
        }
    }
    
    
    @Test
    public void testSaveVictim() {
        DisasterVictim newVictim = new DisasterVictim("Test Person", "2025-03-15");
        newVictim.setGender("non-binary");
        
        try {
            boolean result = dbManager.saveVictim(newVictim);
            assertTrue("Victim should be saved successfully", result);
            
            // Verify victim exists in database
            ArrayList<DisasterVictim> victims = dbManager.loadVictims();
            boolean found = false;
            for (DisasterVictim v : victims) {
                if (v.getFirstName().equals("Test Person")) {
                    found = true;
                    assertEquals("Gender should match", "non-binary person", v.getGender());
                    break;
                }
            }
            assertTrue("Saved victim should be found in database", found);
        } catch (SQLException e) {
            fail("Exception thrown when saving victim: " + e.getMessage());
        }
    }
    
    
    @Test
    public void testHandleDatabaseError() {
        // Test with invalid connection string to force error
        DatabaseManager invalidManager = new DatabaseManager("jdbc:sqlite:invalid/path/db.sqlite");
        
        try {
            // This should write to error log instead of throwing exception
            invalidManager.loadVictims();
            
            // Check that error log file exists
            File errorLog = new File("data/errorlog.txt");
            assertTrue("Error log file should be created", errorLog.exists());
            
        } catch (SQLException e) {
            // SQLException should be caught inside the manager and logged
            fail("Exception should be logged, not thrown: " + e.getMessage());
        }
    }
}