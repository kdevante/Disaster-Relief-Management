
package edu.ucalgary.oop;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class InquiryTest {
    private Inquiry Inquiry;
    private Inquirer inquirer;
    private DisasterVictim missingPerson;
    private Location lastKnownLocation;
    private String validDate = "2025-02-10";
    private String invalidDate = "2025/02/10";
    private String expectedInfoProvided = "Looking for family member";
    private String expectedLogDetails = "Inquirer: John, Missing Person: Jane Alex, Date of Inquiry: 2025-02-10, Info Provided: Looking for family member, Last Known Location: University of Calgary"; 
    victimInquirer = new DisasterVictim("Sarah", "2025-01-25");

    @Before
    public void setUp() {
        // Assuming Inquirer, DisasterVictim, and Location have constructors as implied
        inquirer = new Inquirer("John", "Alex", "1234567890", "Looking for family member");
        missingPerson = new DisasterVictim("Jane Alex", "2025-01-25");
        lastKnownLocation = new Location("University of Calgary", "2500 University Dr NW");
        Inquiry = new Inquiry(inquirer, missingPerson, validDate, expectedInfoProvided, lastKnownLocation);
    }

    @Test
    public void testObjectCreation() {
        assertNotNull("Inquiry object should not be null", Inquiry);
    }

    @Test
    public void testGetInquirer() {
        assertEquals("Inquirer should match the one set in setup", inquirer, Inquiry.getInquirer());
    }

    @Test
    public void testGetMissingPerson() {
        assertEquals("Missing person should match the one set in setup", missingPerson, Inquiry.getMissingPerson());
    }

    @Test
    public void testGetDateOfInquiry() {
        assertEquals("Date of inquiry should match the one set in setup", validDate, Inquiry.getDateOfInquiry());
    }

    @Test
    public void testGetInfoProvided() {
        assertEquals("Info provided should match the one set in setup", expectedInfoProvided, Inquiry.getInfoProvided());
    }

    @Test
    public void testGetLastKnownLocation() {
        assertEquals("Last known location should match the one set in setup", lastKnownLocation, Inquiry.getLastKnownLocation());
    }

    @Test
    public void testSetDateOfInquiryWithValidDate() {
        Inquiry.setDateOfInquiry(validDate);
        assertEquals("Setting a valid date should update the date of inquiry", validDate, inquiry.getDateOfInquiry());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDateOfInquiryWithInvalidDate() {
        Inquiry.setDateOfInquiry(invalidDate); // This should throw IllegalArgumentException due to invalid format
    }

    @Test
    public void testGetLogDetails() {
        assertEquals("Log details should match the expected format", expectedLogDetails, inquiry.getLogDetails());
    }
    @Test
    public void testInquiryFromVictim() {
        // Create an inquiry where a DisasterVictim is the inquirer
        Inquiry victimInquiry = new Inquiry(victimInquirer, missingPerson, validDate, "Looking for my sister", lastKnownLocation);
        
        assertEquals("Inquiry inquirer should be the victim", victimInquirer, victimInquiry.getInquirer());
        assertEquals("Inquirer type should be 'victim'", "victim", victimInquiry.getInquirerType());
    }

    @Test
    public void testInquiryFromExternalInquirer() {
        assertEquals("Inquirer type should be 'external'", "external", inquiry.getInquirerType());
    }

}
