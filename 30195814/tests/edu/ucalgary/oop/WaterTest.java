
public class WaterTest {
    private Water water;
    private String date = "01-02-2024";
    
    @Before
    public void setUp() {
        water = new Water(date);
    }
    
    @Test
    public void testObjectCreation() {
        assertNotNull("Water object should be created successfully", water);
        assertEquals("Type should be set to 'water'", "water", water.getType());
    }
    
    @Test
    public void testAllocationDate() {
        // Initial allocation date should be null
        assertNull("Initial allocation date should be null", water.getAllocationDate());
        
        DisasterVictim victim = new DisasterVictim("John", "2025-01-19");
        Location location = new Location("Shelter A", "123 Main St");
        
        // Allocate water to location first
        water.allocateToLocation(location);
        
        // Allocation date should still be null for location allocation
        assertNull("Allocation date should be null for location allocation", water.getAllocationDate());
        
        victim.addSupply(water);
        
        // Allocation date should be set to today when allocated to victim
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertEquals("Allocation date should be set to today", today, water.getAllocationDate());
    }
    
    @Test
    public void testIsExpired() {
        // Water should not be expired initially
        assertFalse("New water should not be expired", water.isExpired());
        
        // Set allocation date to yesterday
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        water.setAllocationDate(yesterday);
        
        // Water should be expired after 1 day when allocated to a victim
        assertTrue("Water should be expired after 1 day", water.isExpired());
        
        // Water allocated to a location should never expire
        Location location = new Location("Shelter A", "123 Main St");
        location.addSupplies(water);
        
        // Allocation date should be cleared when allocated to location
        assertNull("Allocation date should be null for location allocation", water.getAllocationDate());
        assertFalse("Water allocated to location should not expire", water.isExpired());
    }
}