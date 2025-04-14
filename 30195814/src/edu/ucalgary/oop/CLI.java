package edu.ucalgary.oop;

import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Command-Line Interface for the Disaster Management System.
 * Provides user interaction for all system features.
 * 
 * @author [Devante Kwizera]
 * @version 1.0
 * @since 2025-04-14
 */
public class CLI {
    private Scanner scanner;
    private LanguageManager lang;
    private DatabaseManager dbManager;
    private ArrayList<DisasterVictim> persons;
    private ArrayList<Location> locations;
    private ArrayList<Supply> supplies;
    private ArrayList<ReliefService> inquiries;
    private ArrayList<FamilyGroup> familyGroups;
    
    /**
     * Constructor initializes the CLI with a specific language.
     * 
     * @param languageCode The language code to use
     */
    public CLI(String languageCode) {
        this.scanner = new Scanner(System.in);
        this.lang = new LanguageManager(languageCode);
        this.dbManager = new DatabaseManager();
        this.persons = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.supplies = new ArrayList<>();
        this.inquiries = new ArrayList<>();
        this.familyGroups = new ArrayList<>();
    }
    
    /**
     * Initializes the system and loads data from the database.
     */
    public void initialize() {
        System.out.println(lang.getString("app_name"));
        System.out.println("=".repeat(30));
        System.out.println(lang.getString("loading_data"));
        
        if (!dbManager.createConnection()) {
            System.err.println(lang.getString("error_database_connection"));
            return;
        }
        
        // Load all data from the database
        try {
            loadData();
            System.out.println(lang.getString("operation_successful"));
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            dbManager.logError("Error during initialization", e);
        }
    }
    
    /**
     * Loads all data from the database.
     */
    private void loadData() {
        // Clear existing data
        persons.clear();
        locations.clear();
        supplies.clear();
        inquiries.clear();
        familyGroups.clear();
        
        // Remove expired water supplies
        dbManager.removeExpiredWater();
        
        // Load locations
        locations.addAll(dbManager.loadLocations());
        System.out.println("Loaded " + locations.size() + " locations");
        
        // Load persons
        persons.addAll(dbManager.loadPersons());
        System.out.println("Loaded " + persons.size() + " people");
        
        // Load family groups
        // This would be done as part of loading persons in a real implementation
        
        // Load supplies
        supplies.addAll(dbManager.loadSupplies());
        System.out.println("Loaded " + supplies.size() + " supplies");
        
        // Load inquiries
        // This would be done in a real implementation
        
        // Link data relationships
        // This would be done in a real implementation
    }
    
    /**
     * Starts the main menu loop.
     */
    public void run() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            int choice = getIntInput(1, 5);
            
            switch (choice) {
                case 1:
                    managePeople();
                    break;
                case 2:
                    // manageLocations();
                    System.out.println("Location management not implemented yet");
                    waitForEnter();
                    break;
                case 3:
                    // manageSupplies();
                    System.out.println("Supply management not implemented yet");
                    waitForEnter();
                    break;
                case 4:
                    // manageInquiries();
                    System.out.println("Inquiry management not implemented yet");
                    waitForEnter();
                    break;
                case 5:
                    running = confirmExit();
                    break;
            }
        }
        
        // Close scanner and database connection
        scanner.close();
        dbManager.closeConnection();
        System.out.println(lang.getString("operation_successful"));
    }
    
    /**
     * Displays the main menu options.
     */
    private void displayMainMenu() {
        System.out.println("\n" + lang.getString("main_menu_title"));
        System.out.println("=".repeat(30));
        System.out.println("1. " + lang.getString("menu_manage_people"));
        System.out.println("2. " + lang.getString("menu_manage_locations"));
        System.out.println("3. " + lang.getString("menu_manage_supplies"));
        System.out.println("4. " + lang.getString("menu_manage_inquiries"));
        System.out.println("5. " + lang.getString("menu_exit"));
        System.out.println();
        System.out.print(lang.getString("select_option", 5) + " ");
    }
    
    /**
     * Manages Victim-related operations.
     */
    private void managePeople() {
        boolean managing = true;
        
        while (managing) {
            System.out.println("\n" + lang.getString("people_menu_title"));
            System.out.println("=".repeat(30));
            System.out.println("1. " + lang.getString("add_person"));
            System.out.println("2. " + lang.getString("edit_person"));
            System.out.println("3. " + lang.getString("view_person"));
            System.out.println("4. " + lang.getString("back_to_main"));
            System.out.println();
            System.out.print(lang.getString("select_option", 4) + " ");
            
            int choice = getIntInput(1, 4);
            
            switch (choice) {
                case 1:
                    handlePerson(null); // Add new person
                    break;
                case 2:
                    // Edit person - first select a person then edit
                    DisasterVictim personToEdit = selectPerson();
                    if (personToEdit != null) {
                        handlePerson(personToEdit);
                    }
                    break;
                case 3:
                    viewPerson();
                    break;
                case 4:
                    managing = false;
                    break;
            }
        }
    }
    
    /**
     * Selects a person from the list.
     * 
     * @return The selected person or null if none
     */
    private DisasterVictim selectPerson() {
        System.out.println("\n" + lang.getString("select_person"));
        System.out.println("=".repeat(30));
        
        // List all people
        if (persons.isEmpty()) {
            System.out.println(lang.getString("not_found"));
            waitForEnter();
            return null;
        }
        
        for (int i = 0; i < persons.size(); i++) {
            DisasterVictim person = persons.get(i);
            System.out.println((i + 1) + ". " + person.getFirstName() + " " + 
                            (person.getLastName() != null ? person.getLastName() : ""));
        }
        
        System.out.print(lang.getString("select_option", persons.size()) + " ");
        int index = getIntInput(1, persons.size()) - 1;
        
        return persons.get(index);
    }
    
    /**
     * Handles adding or editing a person.
     * 
     * @param existingPerson The person to edit, or null to add a new person
     */
    private void handlePerson(DisasterVictim existingPerson) {
        boolean isEditing = (existingPerson != null);
        
        System.out.println("\n" + (isEditing ? lang.getString("edit_person") : lang.getString("add_person")));
        System.out.println("=".repeat(30));
        
        // Display current values if editing
        if (isEditing) {
            System.out.println(lang.getString("current_values") + ":");
            System.out.println(lang.getString("first_name_prompt") + " " + existingPerson.getFirstName());
            System.out.println(lang.getString("last_name_prompt") + " " + 
                            (existingPerson.getLastName() != null ? existingPerson.getLastName() : ""));
            System.out.println(lang.getString("gender_prompt") + " " + 
                            (existingPerson.getGender() != null ? existingPerson.getGender() : ""));
            System.out.println(lang.getString("date_of_birth_prompt") + " " + 
                            (existingPerson.getDateOfBirth() != null ? existingPerson.getDateOfBirth() : ""));
            
            System.out.println("\n" + lang.getString("new_values") + ":");
            System.out.println(lang.getString("leave_empty_to_keep") + "\n");
        }
        
        // Get first name
        System.out.print(lang.getString("first_name_prompt") + " ");
        String firstName = scanner.nextLine().trim();
        
        if (firstName.isEmpty() && !isEditing) {
            System.out.println(lang.getString("error_required_field"));
            waitForEnter();
            return;
        } else if (firstName.isEmpty() && isEditing) {
            firstName = existingPerson.getFirstName();
        }
        
        // Get last name
        System.out.print(lang.getString("last_name_prompt") + " ");
        String lastName = scanner.nextLine().trim();
        
        if (lastName.isEmpty() && isEditing && existingPerson.getLastName() != null) {
            lastName = existingPerson.getLastName();
        }
        
        // Get gender
        System.out.println(lang.getString("gender_prompt"));
        System.out.println("1. " + lang.getString("gender_man"));
        System.out.println("2. " + lang.getString("gender_woman"));
        System.out.println("3. " + lang.getString("gender_nb"));
        
        if (isEditing) {
            System.out.println("4. " + lang.getString("leave_unchanged"));
        }
        
        System.out.print(lang.getString("select_option", isEditing ? 4 : 3) + " ");
        String genderInput = scanner.nextLine().trim();
        String gender = null;
        
        if (!genderInput.isEmpty()) {
            try {
                int genderChoice = Integer.parseInt(genderInput);
                if (genderChoice >= 1 && genderChoice <= 3) {
                    switch (genderChoice) {
                        case 1:
                            gender = "man";
                            break;
                        case 2:
                            gender = "woman";
                            break;
                        case 3:
                            gender = "non-binary person";
                            break;
                    }
                } else if (isEditing && genderChoice == 4) {
                    gender = existingPerson.getGender();
                }
            } catch (NumberFormatException e) {
                // Invalid input - will use null or existing value
            }
        }
        
        if (gender == null && isEditing) {
            gender = existingPerson.getGender();
        }
        
        // Get date of birth
        System.out.print(lang.getString("date_of_birth_prompt") + " ");
        String dob = scanner.nextLine().trim();
        
        if (dob.isEmpty() && isEditing && existingPerson.getDateOfBirth() != null) {
            dob = existingPerson.getDateOfBirth();
        }
        
        try {
            DisasterVictim person;
            
            if (isEditing) {
                // Update existing person
                person = existingPerson;
                person.setFirstName(firstName);
                
                if (!lastName.isEmpty()) {
                    person.setLastName(lastName);
                }
                
                if (gender != null) {
                    person.setGender(gender);
                }
                
                if (!dob.isEmpty()) {
                    person.setDateOfBirth(dob);
                }
            } else {
                // Create new person
                String entryDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
                
                if (dob.isEmpty()) {
                    person = new DisasterVictim(firstName, entryDate);
                } else {
                    person = new DisasterVictim(firstName, entryDate, dob);
                }
                
                if (!lastName.isEmpty()) {
                    person.setLastName(lastName);
                }
                
                if (gender != null) {
                    person.setGender(gender);
                }
                
                // Add to list
                persons.add(person);
            }
            
            // Save to database
            boolean saved = dbManager.savePerson(person);
            
            if (saved) {
                System.out.println(lang.getString("operation_successful"));
            } else {
                System.out.println(lang.getString("operation_failed"));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        
        waitForEnter();
    }
    
    /**
     * Views details of a selected person.
     */
    private void viewPerson() {
        DisasterVictim person = selectPerson();
        if (person == null) {
            return;
        }
        
        System.out.println("\n" + lang.getString("view_person") + ": " + 
                         person.getFirstName() + " " + (person.getLastName() != null ? person.getLastName() : ""));
        System.out.println("=".repeat(30));
        
        // Display basic details
        System.out.println(lang.getString("first_name_prompt") + " " + person.getFirstName());
        System.out.println(lang.getString("last_name_prompt") + " " + 
                         (person.getLastName() != null ? person.getLastName() : ""));
        System.out.println(lang.getString("gender_prompt") + " " + 
                         (person.getGender() != null ? person.getGender() : ""));
        System.out.println(lang.getString("date_of_birth_prompt") + " " + 
                         (person.getDateOfBirth() != null ? person.getDateOfBirth() : ""));
        System.out.println(lang.getString("entry_date_prompt") + " " + person.getEntryDate());
        
        // Family group
        if (person.getFamilyGroup() != null) {
            System.out.println("\n" + lang.getString("family_group_prompt") + " " + 
                             person.getFamilyGroup().getGroupId());
        }
        
        // Medical records
        MedicalRecord[] records = person.getMedicalRecords();
        if (records != null && records.length > 0) {
            System.out.println("\nMedical Records:");
            for (MedicalRecord record : records) {
                System.out.println("- " + record.getDateOfTreatment() + ": " + 
                                 record.getTreatmentDetails());
            }
        }
        
        // Personal belongings
        Supply[] belongings = person.getPersonalBelongings();
        if (belongings != null && belongings.length > 0) {
            System.out.println("\nPersonal Belongings:");
            for (Supply item : belongings) {
                if (item instanceof PersonalBelonging) {
                    System.out.println("- " + ((PersonalBelonging) item).getDescription());
                } else {
                    System.out.println("- " + item.getType());
                }
            }
        }
        
        waitForEnter();
    }
    
    /**
     * Confirms if the user wants to exit the program.
     * 
     * @return true if user confirms exit, false otherwise
     */
    private boolean confirmExit() {
        System.out.print("\n" + lang.getString("confirm_exit") + " ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("n") || input.equals("no") || input.equals("non");
    }
    
    /**
     * Gets an integer input within a specified range.
     * 
     * @param min Minimum valid value
     * @param max Maximum valid value
     * @return The validated input
     */
    private int getIntInput(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.print(lang.getString("invalid_option") + " ");
                }
            } catch (NumberFormatException e) {
                System.out.print(lang.getString("invalid_option") + " ");
            }
        }
    }
    
    /**
     * Waits for the user to press Enter to continue.
     */
    private void waitForEnter() {
        System.out.print("\n" + lang.getString("press_enter") + " ");
        scanner.nextLine();
    }

}