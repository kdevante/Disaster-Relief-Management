package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles all database operations for the disaster management system.
 * Manages connection, CRUD operations, and data loading/saving.
 * 
 * @author [Devante Kwizera]
 * @version 1.0
 * @since 2025-04-13
 */
public class DatabaseManager {
    private Connection dbConnect;
    private ResultSet results;
    private final String url;
    private final String username;
    private final String password;
    private final String ERROR_LOG_PATH = "data/errorlog.txt";

    /**
     * Constructor with default PostgreSQL connection settings
     */
    public DatabaseManager() {
        this.url = "jdbc:postgresql://localhost:5432/project";
        this.username = "oop";
        this.password = "ucalgary";
    }

    /**
     * Creates a connection to the database
     * 
     * @return true if connection successful, false otherwise
     */
    public boolean createConnection() {
        try {
            dbConnect = DriverManager.getConnection(url, username, password);
            return true;
        } catch (SQLException e) {
            logError("Failed to connect to database", e);
            return false;
        }
    }

    /**
     * Closes the database connection
     */
    public void closeConnection() {
        try {
            if (dbConnect != null) {
                dbConnect.close();
            }
        } catch (SQLException e) {
            logError("Failed to close database connection", e);
        }
    }

    /**
     * Logs an error to the error log file
     * 
     * @param message Error message
     * @param e Exception that occurred
     */
    public void logError(String message, Exception e) {
        try (FileWriter fw = new FileWriter(ERROR_LOG_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            String timestamp = LocalDate.now() + " " + java.time.LocalTime.now();
            pw.println("[" + timestamp + "] " + message + ": " + e.getMessage());
            e.printStackTrace(pw);
            pw.println("-------------------------");
            
        } catch (IOException ioEx) {
            System.err.println("Failed to write to error log: " + ioEx.getMessage());
        }
    }

    /**
     * Checks if database connection is active
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return dbConnect != null && !dbConnect.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Removes expired water supplies from the database
     */
    public void removeExpiredWater() {
        if (!isConnected() && !createConnection()) {
            return;
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);
        String sql = "DELETE FROM SupplyAllocation WHERE supply_id IN " +
                     "(SELECT supply_id FROM Supply WHERE type = 'water') " +
                     "AND person_id IS NOT NULL " +
                     "AND allocation_date < ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(yesterday));
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Removed " + rowsAffected + " expired water supplies");
        } catch (SQLException e) {
            logError("Failed to remove expired water", e);
        }
    }

    /**
     * Loads all persons from the database
     * 
     * @return ArrayList of DisasterVictim objects
     */
    public ArrayList<DisasterVictim> loadPersons() {
        ArrayList<DisasterVictim> persons = new ArrayList<>();
        if (!isConnected() && !createConnection()) {
            return persons;
        }

        String sql = "SELECT * FROM Person";

        try (Statement stmt = dbConnect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Get person data from result set
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                Date dobDate = rs.getDate("date_of_birth");
                String gender = rs.getString("gender");
                String comments = rs.getString("comments");
                String phone = rs.getString("phone_number");
                int familyGroup = rs.getInt("family_group");
                
                // Create person using current date as entry date (not in DB schema)
                String entryDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
                DisasterVictim person = new DisasterVictim(firstName, entryDate);
                
                // Set optional fields
                if (lastName != null) {
                    person.setLastName(lastName);
                }
                
                if (dobDate != null) {
                    person.setDateOfBirth(dobDate.toString());
                }
                
                if (gender != null) {
                    person.setGender(gender);
                }
                
                if (comments != null) {
                    person.setComments(comments);
                }
                
                // TODO: Handle family group loading
                
                persons.add(person);
            }
        } catch (SQLException e) {
            logError("Failed to load persons", e);
        }
        
        return persons;
    }

 
    /**
     * Loads all locations from the database
     * 
     * @return ArrayList of Location objects
     */
    public ArrayList<Location> loadLocations() {
        ArrayList<Location> locations = new ArrayList<>();
        if (!isConnected() && !createConnection()) {
            return locations;
        }

        String sql = "SELECT * FROM Location";

        try (Statement stmt = dbConnect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String address = rs.getString("address");
                
                Location location = new Location(name, address);
                locations.add(location);
            }
        } catch (SQLException e) {
            logError("Failed to load locations", e);
        }
        
        return locations;
    }

    /**
     * Loads all supplies from the database
     * 
     * @return ArrayList of Supply objects
     */
    public ArrayList<Supply> loadSupplies() {
        ArrayList<Supply> supplies = new ArrayList<>();
        if (!isConnected() && !createConnection()) {
            return supplies;
        }

        String sql = "SELECT * FROM Supply";

        try (Statement stmt = dbConnect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int supplyId = rs.getInt("supply_id");
                String type = rs.getString("type");
                String comments = rs.getString("comments");
                
                Supply supply;
                
                // Create appropriate supply type based on 'type' field
                switch (type.toLowerCase()) {
                    case "water":
                        supply = new Water(LocalDate.now(), 1);
                        break;
                    case "blanket":
                        supply = new Blanket(1);
                        break;
                    case "cot":
                        // Parse room and grid from comments if available
                        String room = "101";
                        String grid = "A1";
                        if (comments != null && comments.matches("\\d+ [A-Z]\\d+")) {
                            String[] parts = comments.split(" ");
                            room = parts[0];
                            grid = parts[1];
                        }
                        supply = new Cot(Integer.parseInt(room), grid, 1);
                        break;
                    case "personal item":
                        supply = new PersonalBelonging(comments != null ? comments : "Unknown", 1);
                        break;
                    default:
                        supply = new Supply(type, 1);
                }
                
                supplies.add(supply);
            }
        } catch (SQLException e) {
            logError("Failed to load supplies", e);
        }
        
        return supplies;
    }

    /**
     * Loads all inquiries from the database
     * 
     * @param persons Map of loaded persons by ID
     * @param locations Map of loaded locations by ID
     * @return ArrayList of ReliefService objects
     */
    public ArrayList<ReliefService> loadInquiries(Map<Integer, DisasterVictim> persons, 
                                                  Map<Integer, Location> locations) {
        ArrayList<ReliefService> inquiries = new ArrayList<>();
        if (!isConnected() && !createConnection()) {
            return inquiries;
        }

        String sql = "SELECT * FROM Inquiry";

        try (Statement stmt = dbConnect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int inquirerId = rs.getInt("inquirer_id");
                int seekingId = rs.getInt("seeking_id");
                int locationId = rs.getInt("location_id");
                Timestamp inquiryDate = rs.getTimestamp("date_of_inquiry");
                String comments = rs.getString("comments");
                
                // Skip if we don't have the referenced entities
                if (!persons.containsKey(inquirerId) || 
                    !persons.containsKey(seekingId) || 
                    !locations.containsKey(locationId)) {
                    continue;
                }
                
                // Get the referenced entities
                DisasterVictim inquirer = persons.get(inquirerId);
                DisasterVictim seeking = persons.get(seekingId);
                Location location = locations.get(locationId);
                
                // Create inquiry object
                String dateStr = inquiryDate.toLocalDateTime().toLocalDate().toString();
                
                // Create inquirer object (can be a DisasterVictim or external Inquirer)
                Inquirer inquirerObj = new Inquirer(inquirer.getFirstName(), 
                                                    inquirer.getLastName(),
                                                    "", // Phone number not in Person table
                                                    comments != null ? comments : "");
                
                ReliefService inquiry = new ReliefService(
                    inquirerObj, seeking, dateStr, 
                    comments != null ? comments : "", location);
                
                inquiries.add(inquiry);
            }
        } catch (SQLException e) {
            logError("Failed to load inquiries", e);
        }
        
        return inquiries;
    }

    /**
     * Loads medical records from the database
     * 
     * @param persons Map of loaded persons by ID
     * @param locations Map of loaded locations by ID
     */
    public void loadMedicalRecords(Map<Integer, DisasterVictim> persons, 
                                  Map<Integer, Location> locations) {
        if (!isConnected() && !createConnection()) {
            return;
        }

        String sql = "SELECT * FROM MedicalRecord";

        try (Statement stmt = dbConnect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int personId = rs.getInt("person_id");
                int locationId = rs.getInt("location_id");
                Timestamp treatmentDate = rs.getTimestamp("date_of_treatment");
                String treatmentDetails = rs.getString("treatment_details");
                
                // Skip if we don't have the referenced entities
                if (!persons.containsKey(personId) || !locations.containsKey(locationId)) {
                    continue;
                }
                
                // Get the referenced entities
                DisasterVictim person = persons.get(personId);
                Location location = locations.get(locationId);
                
                // Create and add medical record
                String dateStr = treatmentDate.toLocalDateTime().toLocalDate().toString();
                MedicalRecord record = new MedicalRecord(location, treatmentDetails, dateStr);
                
                person.addMedicalRecord(record);
            }
        } catch (SQLException e) {
            logError("Failed to load medical records", e);
        }
    }

    /**
     * Saves a DisasterVictim to the database
     * 
     * @param victim The disaster victim to save
     * @return true if successful, false otherwise
     */
    public boolean savePerson(DisasterVictim victim) {
        if (!isConnected() && !createConnection()) {
            return false;
        }

        // Check if person already exists (by social ID)
        int personId = getPersonId(victim);
        
        if (personId > 0) {
            // Update existing person
            return updatePerson(personId, victim);
        } else {
            // Insert new person
            return insertPerson(victim);
        }
    }

    /**
     * Gets the database ID for a person
     * 
     * @param victim The disaster victim
     * @return The database ID or -1 if not found
     */
    private int getPersonId(DisasterVictim victim) {
        String sql = "SELECT person_id FROM Person WHERE first_name = ? AND last_name = ?";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setString(1, victim.getFirstName());
            stmt.setString(2, victim.getLastName());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("person_id");
                }
            }
        } catch (SQLException e) {
            logError("Failed to get person ID", e);
        }
        
        return -1;
    }

    /**
     * Updates an existing person in the database
     * 
     * @param personId The database ID
     * @param victim The disaster victim
     * @return true if successful, false otherwise
     */
    private boolean updatePerson(int personId, DisasterVictim victim) {
        String sql = "UPDATE Person SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                     "gender = ?, comments = ?, phone_number = ?, family_group = ? " +
                     "WHERE person_id = ?";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setString(1, victim.getFirstName());
            stmt.setString(2, victim.getLastName());
            
            // Convert date of birth to SQL Date
            if (victim.getDateOfBirth() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(victim.getDateOfBirth()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            
            stmt.setString(4, victim.getGender());
            stmt.setString(5, victim.getComments());
            
            // Phone number not in DisasterVictim class
            stmt.setNull(6, java.sql.Types.VARCHAR);
            
            // Family group
            FamilyGroup group = victim.getFamilyGroup();
            if (group != null) {
                stmt.setInt(7, Integer.parseInt(group.getGroupId()));
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(8, personId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Failed to update person", e);
            return false;
        }
    }
    /**
     * Inserts a new person into the database
     * 
     * @param victim The disaster victim
     * @return true if successful, false otherwise
     */
    private boolean insertPerson(DisasterVictim victim) {
        String sql = "INSERT INTO Person (first_name, last_name, date_of_birth, gender, " +
                     "comments, phone_number, family_group) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, victim.getFirstName());
            stmt.setString(2, victim.getLastName());
            
            // Convert date of birth to SQL Date
            if (victim.getDateOfBirth() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(victim.getDateOfBirth()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            
            stmt.setString(4, victim.getGender());
            stmt.setString(5, victim.getComments());
            
            // Phone number not in DisasterVictim class
            stmt.setNull(6, java.sql.Types.VARCHAR);
            
            // Family group
            FamilyGroup group = victim.getFamilyGroup();
            if (group != null) {
                stmt.setInt(7, Integer.parseInt(group.getGroupId()));
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            // Get the generated ID
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // We could store this ID, but it's not currently needed
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            logError("Failed to insert person", e);
            return false;
        }
    }

    /**
     * Saves a location to the database
     * 
     * @param location The location to save
     * @return true if successful, false otherwise
     */
    public boolean saveLocation(Location location) {
        if (!isConnected() && !createConnection()) {
            return false;
        }

        // Check if location already exists
        int locationId = getLocationId(location);
        
        if (locationId > 0) {
            // Update existing location
            return updateLocation(locationId, location);
        } else {
            // Insert new location
            return insertLocation(location);
        }
    }

    /**
     * Gets the database ID for a location
     * 
     * @param location The location
     * @return The database ID or -1 if not found
     */
    private int getLocationId(Location location) {
        String sql = "SELECT location_id FROM Location WHERE name = ?";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setString(1, location.getName());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("location_id");
                }
            }
        } catch (SQLException e) {
            logError("Failed to get location ID", e);
        }
        
        return -1;
    }

    /**
     * Updates an existing location in the database
     * 
     * @param locationId The database ID
     * @param location The location
     * @return true if successful, false otherwise
     */
    private boolean updateLocation(int locationId, Location location) {
        String sql = "UPDATE Location SET name = ?, address = ? WHERE location_id = ?";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setString(1, location.getName());
            stmt.setString(2, location.getAddress());
            stmt.setInt(3, locationId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Failed to update location", e);
            return false;
        }
    }

    /**
     * Inserts a new location into the database
     * 
     * @param location The location
     * @return true if successful, false otherwise
     */
    private boolean insertLocation(Location location) {
        String sql = "INSERT INTO Location (name, address) VALUES (?, ?)";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setString(1, location.getName());
            stmt.setString(2, location.getAddress());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Failed to insert location", e);
            return false;
        }
    }

    /**
     * Saves a supply to the database
     * 
     * @param supply The supply to save
     * @return true if successful, false otherwise
     */
    public boolean saveSupply(Supply supply) {
        if (!isConnected() && !createConnection()) {
            return false;
        }

        String sql = "INSERT INTO Supply (type, comments) VALUES (?, ?)";
        String comments = null;
        
        // Get comments based on supply type
        if (supply instanceof PersonalBelonging) {
            comments = ((PersonalBelonging) supply).getDescription();
        } else if (supply instanceof Cot) {
            Cot cot = (Cot) supply;
            comments = cot.getRoom() + " " + cot.getGrid();
        }
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setString(1, supply.getType());
            
            if (comments != null) {
                stmt.setString(2, comments);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Failed to save supply", e);
            return false;
        }
    }

    /**
     * Allocates a supply to a person or location
     * 
     * @param supply The supply
     * @param person The person (null if allocating to location)
     * @param location The location
     * @return true if successful, false otherwise
     */
    public boolean allocateSupply(Supply supply, DisasterVictim person, Location location) {
        if (!isConnected() && !createConnection()) {
            return false;
        }

        // Get IDs
        int supplyId = getSupplyId(supply);
        int personId = person != null ? getPersonId(person) : -1;
        int locationId = getLocationId(location);
        
        if (supplyId <= 0 || (personId <= 0 && locationId <= 0)) {
            return false;
        }

        String sql = "INSERT INTO SupplyAllocation (supply_id, person_id, location_id, allocation_date) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setInt(1, supplyId);
            
            if (personId > 0) {
                stmt.setInt(2, personId);
                stmt.setNull(3, java.sql.Types.INTEGER);
                
                // Set allocation date for person (for water expiry)
                if (supply instanceof Water) {
                    ((Water) supply).setAllocationDate(LocalDate.now());
                }
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
                stmt.setInt(3, locationId);
            }
            
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Failed to allocate supply", e);
            return false;
        }
    }

    /**
     * Gets the database ID for a supply
     * 
     * @param supply The supply
     * @return The database ID or -1 if not found
     */
    private int getSupplyId(Supply supply) {
        // This is a simplified approach - in a real system, you'd need a better way to identify supplies
        String sql = "SELECT supply_id FROM Supply WHERE type = ?";
        
        if (supply instanceof PersonalBelonging) {
            sql += " AND comments = ?";
        }
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setString(1, supply.getType());
            
            if (supply instanceof PersonalBelonging) {
                stmt.setString(2, ((PersonalBelonging) supply).getDescription());
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("supply_id");
                }
            }
        } catch (SQLException e) {
            logError("Failed to get supply ID", e);
        }
        
        return -1;
    }

    /**
     * Saves an inquiry to the database
     * 
     * @param inquiry The inquiry to save
     * @return true if successful, false otherwise
     */
    public boolean saveInquiry(ReliefService inquiry) {
        if (!isConnected() && !createConnection()) {
            return false;
        }

        // Get IDs
        Inquirer inquirer = inquiry.getInquirer();
        DisasterVictim seeking = inquiry.getMissingPerson();
        Location location = inquiry.getLastKnownLocation();
        
        // This is complex because we need to first ensure the inquirer exists
        // For simplicity, we assume the inquirer, seeking person, and location already exist
        
        int inquirerId = getInquirerId(inquirer);
        int seekingId = getPersonId(seeking);
        int locationId = getLocationId(location);
        
        if (inquirerId <= 0 || seekingId <= 0 || locationId <= 0) {
            return false;
        }

        String sql = "INSERT INTO Inquiry (inquirer_id, seeking_id, location_id, date_of_inquiry, comments) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setInt(1, inquirerId);
            stmt.setInt(2, seekingId);
            stmt.setInt(3, locationId);
            
            // Parse date
            LocalDate date = LocalDate.parse(inquiry.getDateOfInquiry(), DateTimeFormatter.ISO_DATE);
            stmt.setDate(4, java.sql.Date.valueOf(date));
            
            stmt.setString(5, inquiry.getInfoProvided());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Failed to save inquiry", e);
            return false;
        }
    }

    /**
     * Gets the database ID for an inquirer
     * 
     * @param inquirer The inquirer
     * @return The database ID or -1 if not found
     */
    private int getInquirerId(Inquirer inquirer) {
        // For simplicity, we assume the inquirer is a person in the database
        String sql = "SELECT person_id FROM Person WHERE first_name = ? AND last_name = ?";
        
        try (PreparedStatement stmt = dbConnect.prepareStatement(sql)) {
            stmt.setString(1, inquirer.getFirstName());
            stmt.setString(2, inquirer.getLastName());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("person_id");
                }
            }
        } catch (SQLException e) {
            logError("Failed to get inquirer ID", e);
        }
        
        return -1;
    }

    /**
     * Loads all data from the database
     * 
     * @return Map containing all loaded data
     */
    public Map<String, Object> loadAllData() {
        Map<String, Object> data = new HashMap<>();
        
        // Remove expired water supplies
        removeExpiredWater();
        
        // Load all persons
        ArrayList<DisasterVictim> persons = loadPersons();
        data.put("persons", persons);
        
        // Create a map for easier lookup
        Map<Integer, DisasterVictim> personMap = new HashMap<>();
        for (DisasterVictim person : persons) {
            personMap.put(person.getAssignedSocialID(), person);
        }
        
        // Load all locations
        ArrayList<Location> locations = loadLocations();
        data.put("locations", locations);
        
        // Create a map for easier lookup
        Map<Integer, Location> locationMap = new HashMap<>();
        for (int i = 0; i < locations.size(); i++) {
            locationMap.put(i + 1, locations.get(i)); // Assuming IDs start from 1
        }
        
        // Load all supplies
        ArrayList<Supply> supplies = loadSupplies();
        data.put("supplies", supplies);
        
        // Load medical records
        loadMedicalRecords(personMap, locationMap);
        
        // Load inquiries
        ArrayList<ReliefService> inquiries = loadInquiries(personMap, locationMap);
        data.put("inquiries", inquiries);
        
        return data;
    }
}