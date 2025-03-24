package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for the PersonalBelonging class
 */
public class PersonalBelongingTest {
    private PersonalBelonging personalBelonging;
    private String description = "Green leather suitcase";
    
    @Before
    public void setUp() {
        personalBelonging = new PersonalBelonging(description);
    }
    
    @Test
    public void testObjectCreation() {
        assertNotNull("PersonalBelonging object should be created successfully", personalBelonging);
        assertEquals("Type should be set to 'personal belonging'", "personal belonging", personalBelonging.getType());
    }
    
    @Test
    public void testGetDescription() {
        assertEquals("getDescription should return the correct description", description, personalBelonging.getDescription());
    }
    
    @Test
    public void testSetDescription() {
        String newDescription = "Red backpack";
        personalBelonging.setDescription(newDescription);
        assertEquals("setDescription should update the description", newDescription, personalBelonging.getDescription());
    }
    
    @Test
    public void testAllocateToVictim() {
        DisasterVictim victim = new DisasterVictim("John", "2025-01-19");
        victim.addSupply(personalBelonging);
        

        assertTrue("Victim's personal belongings should contain this item", victim.getSupplies().contains(personalBelonging));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAllocateToLocation() {
        // Personal belongings cannot be allocated to locations
        Location location = new Location("Shelter A", "123 Main St");
        location.setSupplies(personalBelonging);
        // This should throw an IllegalArgumentException as personal belongings cannot be allocated to locations
    }
}