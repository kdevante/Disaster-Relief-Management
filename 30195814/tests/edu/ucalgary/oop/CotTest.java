package edu.ucalgary.oop;

import org.junit.Test;
import static org.junit.Assert.*;

public class CotTest {
    private Cot cot;
    private String room = "115";
    private String gridLocation = "B6";
    
    @Before
    public void setUp() {
        cot = new Cot(room, gridLocation);
    }
    
    @Test
    public void testObjectCreation() {
        assertNotNull("Cot object should be created successfully", cot);
        assertEquals("Type should be set to 'cot'", "cot", cot.getType());
    }
    
    @Test
    public void testGetRoom() {
        assertEquals("getRoom should return the correct room", room, cot.getRoom());
    }
    
    @Test
    public void testSetRoom() {
        String newRoom = "116";
        cot.setRoom(newRoom);
        assertEquals("setRoom should update the room", newRoom, cot.getRoom());
    }
    
    @Test
    public void testGetGridLocation() {
        assertEquals("getGridLocation should return the correct grid location", gridLocation, cot.getGridLocation());
    }
    
    @Test
    public void testSetGridLocation() {
        String newGridLocation = "C7";
        cot.setGridLocation(newGridLocation);
        assertEquals("setGridLocation should update the grid location", newGridLocation, cot.getGridLocation());
    }
    
    @Test
    public void testAllocation() {
        // Cots should work like regular supplies
        Location location = new Location("Shelter A", "123 Main St");
        
        location.addSupplies(cot);
        assertEquals("Cot should be allocated to the location", location.getSupplies());
        
    }
}