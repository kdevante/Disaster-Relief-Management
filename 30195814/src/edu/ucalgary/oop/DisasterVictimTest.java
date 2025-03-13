package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class DisasterVictimTest {
    private DisasterVictim victim;
    private List<Supply> suppliesToSet; 
    private List<FamilyRelation> familyRelations; 
    private String expectedFirstName = "Freda";
    private String EXPECTED_ENTRY_DATE = "2025-01-18";
    private String validDate = "2025-01-15";
    private String invalidDate = "15/13/2025";
    private String expectedGender = "female"; 
    private String expectedComments = "Needs medical attention and speaks 2 languages";

    @Before
    public void setUp() {
        victim = new DisasterVictim(expectedFirstName, EXPECTED_ENTRY_DATE);
        suppliesToSet = new ArrayList<>();
        suppliesToSet.add(new Supply("Water Bottle", 10));
        suppliesToSet.add(new Supply("Blanket", 5));
        
        DisasterVictim victim1 = new DisasterVictim("Jane", "2025-01-20");
        DisasterVictim victim2 = new DisasterVictim("John", "2025-01-22");
        
    }

  		  
  @Test
  public void testBirthdateConstructorWithValidEntryDate() {
        String validEntryDate = "2025-02-18";
        String validBirthdate = "2017-03-20";
        DisasterVictim victim = new DisasterVictim("Freda", validEntryDate, validBirthdate);
        assertNotNull("Constructor should successfully create an instance with a valid entry date", victim);
        assertEquals("Constructor should set the entry date correctly", validEntryDate, victim.getEntryDate());
        assertEquals("Constructor should set the birth date correctly", validBirthdate, victim.getDateOfBirth());
  }

    @Test(expected = IllegalArgumentException.class)
    public void testBirthdateConstructorWithInvalidEntryDateFormat() {
        String invalidEntryDate = "20250112"; 
        String validBirthdate = "2017-03-20";
        new DisasterVictim("Fang", invalidEntryDate, validBirthdate);
        // Expecting IllegalArgumentException due to invalid date format
    }

  @Test(expected = IllegalArgumentException.class)
  public void testBirthdateConstructorWithInvalidBirthdate() {
        String validEntryDate = "2025-02-18";
        String invalidBirthDate = "20250112"; 
        DisasterVictim victim = new DisasterVictim("Yaw", validEntryDate, invalidBirthDate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBirthdateConstructorWithBirthdateAfterEntryDate() {
        String validEntryDate = "2025-02-17";
        String validBirthDate = "2025-02-18";
        DisasterVictim victim = new DisasterVictim("Jessica", validEntryDate, validBirthDate);
  }


  @Test
    public void testConstructorWithValidEntryDate() {
        String validEntryDate = "2025-01-18";
        DisasterVictim victim = new DisasterVictim("Freda", validEntryDate);
        assertNotNull("Constructor should successfully create an instance with a valid entry date", victim);
        assertEquals("Constructor should set the entry date correctly", validEntryDate, victim.getEntryDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidEntryDateFormat() {
        String invalidEntryDate = "18/01/2025"; // Incorrect format according to your specifications
        new DisasterVictim("Freda", invalidEntryDate);
        // Expecting IllegalArgumentException due to invalid date format
    }


   @Test
    public void testSetDateOfBirth() {
        String newDateOfBirth = "1987-05-21";
        victim.setDateOfBirth(newDateOfBirth);
        assertEquals("setDateOfBirth should correctly update the date of birth", newDateOfBirth, victim.getDateOfBirth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDateOfBirthWithInvalidFormat() {
        victim.setDateOfBirth(invalidDate); // This format should cause an exception
    }
	
	@Test
    public void testSetAndGetFirstName() {
        String newFirstName = "Alice";
        victim.setFirstName(newFirstName);
        assertEquals("setFirstName should update and getFirstName should return the new first name", newFirstName, victim.getFirstName());
    }

    @Test
    public void testSetAndGetLastName() {
        String newLastName = "Smith";
        victim.setLastName(newLastName);
        assertEquals("setLastName should update and getLastName should return the new last name", newLastName, victim.getLastName());
    }

    @Test
    public void testGetComments() {
        victim.setComments(expectedComments);
        assertEquals("getComments should return the initial correct comments", expectedComments, victim.getComments());
    }

    @Test
    public void testSetComments() {
        victim.setComments(expectedComments);
        String newComments = "Has a minor injury on the left arm";
        victim.setComments(newComments);
        assertEquals("setComments should update the comments correctly", newComments, victim.getComments());
    }

    @Test
    public void testGetAssignedSocialID() {
        // The next victim should have an ID one higher than the previous victim
        // Tests can be run in any order so two victims will be created
        DisasterVictim newVictim = new DisasterVictim("Kash", "2025-01-21");
        int expectedSocialId = newVictim.getAssignedSocialID() + 1;
        DisasterVictim actualVictim = new DisasterVictim("Adeleke", "2025-01-22");

        assertEquals("getAssignedSocialID should return the expected social ID", expectedSocialId, actualVictim.getAssignedSocialID());
    }

    @Test
    public void testGetEntryDate() {
        assertEquals("getEntryDate should return the expected entry date", EXPECTED_ENTRY_DATE, victim.getEntryDate());
    }
   
    @Test
    public void testSetAndGetGender() {
        // Test all valid gender options: "man", "woman", "non-binary person"
        victim.setGender("man");
        assertEquals("setGender should accept 'man' as valid input", "man", victim.getGender());
        
        victim.setGender("woman");
        assertEquals("setGender should accept 'woman' as valid input", "woman", victim.getGender());
        
        victim.setGender("non-binary");
        assertEquals("setGender should accept 'non-binary person' as valid input", "non-binary", victim.getGender());
    }
	

@Test
public void testAllocateSupply() {
    
    // Create supplies at the location
    Blanket blanket = new Blanket("B001");
    Water water = new Water();
    Cot cot = new Cot("C001", "115", "B6");
    
    location.addSupply(blanket);
    location.addSupply(water);
    location.addSupply(cot);
    
    // Allocate supplies to victim
    victim.allocateSupply(blanket);
    victim.allocateSupply(water);
    victim.allocateSupply(cot);
    
    // Check that supplies are allocated to victim
    assertTrue("Blanket should be allocated to victim", victim.getSupplies().contains(blanket));
    assertTrue("Water should be allocated to victim", victim.getSupplies().contains(water));
    assertTrue("Cot should be allocated to victim", victim.getSupplies().contains(cot));
    
    
    // Check that supplies are no longer at location
    assertFalse("Blanket should not be at location", location.getSupplies().contains(blanket));
    assertFalse("Water should not be at location", location.getSupplies().contains(water));
    assertFalse("Cot should not be at location", location.getSupplies().contains(cot));
}

@Test
public void testSetAndGetFamilyGroup() {
    FamilyGroup familyGroup = new FamilyGroup("Dalan Family");
    victim.setFamilyGroup(familyGroup);
    assertEquals("getFamilyGroup should return the assigned family group", 
                familyGroup, victim.getFamilyGroup());
    assertTrue("Family group should contain the victim", 
              familyGroup.getMembers().contains(victim));
}

// 3. Replace FamilyRelation tests with FamilyGroup tests
// Replace testAddFamilyConnection with:
@Test
public void testFamilyGroupMembership() {
    DisasterVictim victim1 = new DisasterVictim("Jane", "2025-01-20");
    DisasterVictim victim2 = new DisasterVictim("John", "2025-01-22");
    
    FamilyGroup familyGroup = new FamilyGroup( "Dalan Family");
    familyGroup.addMember(victim1);
    familyGroup.addMember(victim2);
    
    assertEquals("Victim1 should be assigned to the family group", 
                familyGroup, victim1.getFamilyGroup());
    assertEquals("Victim2 should be assigned to the family group", 
                familyGroup, victim2.getFamilyGroup());
    assertTrue("Family group should contain both victims", 
              familyGroup.getMembers().contains(victim1) && 
              familyGroup.getMembers().contains(victim2));
}

  @Test
public void testSetMedicalRecords() {
    Location testLocation = new Location("Shelter Z", "1234 Shelter Ave");
    MedicalRecord testRecord = new MedicalRecord(testLocation, "test for strep", "2025-02-09");
    boolean correct = true;

    MedicalRecord[] newRecords = { testRecord };
    victim.setMedicalRecords(newRecords);
    MedicalRecord[] actualRecords = victim.getMedicalRecords();

    // We have not studied overriding equals in arrays of custom objects so we will manually evaluate equality
    if (newRecords.length != actualRecords.length) {
        correct = false;
    } else {
        int i;
        for (i=0;i<newRecords.length;i++) {
            if (actualRecords[i] != newRecords[i]) {
                correct = false;
            }
        }
    }
    assertTrue("setMedicalRecords should correctly update medical records", correct);
}


    
}





