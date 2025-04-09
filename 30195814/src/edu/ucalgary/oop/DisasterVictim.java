package edu.ucalgary.oop;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Represents a disaster victim in the emergency response system.
 * Each victim has personal details, medical records, and personal belongings.
 * 
 * @author Devante Kwizera
 * @version 1.0
 * @since 2025-04-09
 */
public class DisasterVictim {
    private static int counter = 0;

    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private final int ASSIGNED_SOCIAL_ID;
    private FamilyGroup familyGroup;
    private ArrayList<MedicalRecord> medicalRecords = new ArrayList<>();
    private Supply[] personalBelongings;
    private final String ENTRY_DATE;
    private String gender;
    private String comments;


     /**
     * Constructor for creating a disaster victim with a first name and entry date.
     * @param firstName Victim's first name.
     * @param ENTRY_DATE Date the victim entered the center (format: YYYY-MM-DD).
     * @throws IllegalArgumentException if date format is invalid.
     */
    public DisasterVictim(String firstName, String ENTRY_DATE) throws IllegalArgumentException {
        this.firstName = firstName;
        if (!isValidDateFormat(ENTRY_DATE)) {
            throw new IllegalArgumentException("Invalid date format for entry date. Expected format: YYYY-MM-DD");
        }
        this.ENTRY_DATE = ENTRY_DATE;
        this.ASSIGNED_SOCIAL_ID = generateSocialID();
    }

    /**
     * Constructor for creating a disaster victim with first name, entry date, and date of birth.
     * @param firstName Victim's first name.
     * @param ENTRY_DATE Entry date (format: YYYY-MM-DD).
     * @param dateOfBirth Date of birth (format: YYYY-MM-DD).
     * @throws IllegalArgumentException if format is invalid or birth is after entry.
     */
    public DisasterVictim(String firstName, String ENTRY_DATE, String dateOfBirth) throws IllegalArgumentException {
        this.firstName = firstName;
        if (!isValidDateFormat(ENTRY_DATE)) {
            throw new IllegalArgumentException("Invalid date format for entry date. Expected format: YYYY-MM-DD");
        }
        this.ENTRY_DATE = ENTRY_DATE;
        this.ASSIGNED_SOCIAL_ID = generateSocialID();
        setDateOfBirth(dateOfBirth);
    }


    private static int generateSocialID() {
        counter++;
        return counter;
    }

    private static boolean isValidDateFormat(String date) {
        String dateFormatPattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return date.matches(dateFormatPattern);
    }

    private static int convertDateStringToInt(String dateStr) {
        // Use regex to remove dashes from the date string
        String formattedDate = dateStr.replaceAll("-", "");
        
        // Convert the formatted string to an integer
        return Integer.parseInt(formattedDate);
    }

  
    // Getters and setters

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) throws IllegalArgumentException {
        if (!isValidDateFormat(dateOfBirth)) {
            throw new IllegalArgumentException("Invalid date format for date of birth. Expected format: YYYY-MM-DD");
        }

        // A person cannot be born after entering a centre
        int entryDate = convertDateStringToInt(ENTRY_DATE);
        int birthDate = convertDateStringToInt(dateOfBirth);
        if (birthDate > entryDate) {
            throw new IllegalArgumentException("Birthdate must be the same as or before entry date");
        }
        
        this.dateOfBirth = dateOfBirth;
    }

    public int getAssignedSocialID() {
        return ASSIGNED_SOCIAL_ID;
    }

    public FamilyGroup getFamilyGroup() {
        return familyGroup;
    }

    public MedicalRecord[] getMedicalRecords() {
        return medicalRecords.toArray(new MedicalRecord[0]);
    }

    public Supply[] getPersonalBelongings() {
        return this.personalBelongings;
    }


    public void setFamilyGroup(FamilyGroup group) {
        this.familyGroup = group;
    }

    public void setMedicalRecords(MedicalRecord[] records) {
        this.medicalRecords.clear();
        for (MedicalRecord newRecord : records) {
            addMedicalRecord(newRecord);
        }
    }

    public void setPersonalBelongings(Supply[] belongings) {
        this.personalBelongings = belongings;
    }

    // Add a Supply to personalBelonging
    public void addPersonalBelonging(Supply supply) {

        if (this.personalBelongings == null) {
            Supply tmpSupply[] = { supply };
            this.setPersonalBelongings(tmpSupply);
            return;
        }

        // Create an array one larger than the previous array
        int newLength = this.personalBelongings.length + 1;
        Supply tmpPersonalBelongings[] = new Supply[newLength];

        // Copy all the items in the current array to the new array
        int i;
        for (i=0; i < personalBelongings.length; i++) {
            tmpPersonalBelongings[i] = this.personalBelongings[i];
        }

        // Add the new element at the end of the new array
        tmpPersonalBelongings[i] = supply;

        // Replace the original array with the new array
        this.personalBelongings = tmpPersonalBelongings;
    }

    // Remove a Supply from personalBelongings, we assume it only appears once
    public void removePersonalBelonging(Supply unwantedSupply) {
        Supply[] updatedBelongings = new Supply[personalBelongings.length-1];
        int index = 0;
        int newIndex = index;
        for (Supply supply : personalBelongings) {
            if (!supply.equals(unwantedSupply)) {
                updatedBelongings[newIndex] = supply;
                newIndex++;
            }
            index++;
        }
    }


    // Add a MedicalRecord to medicalRecords
    public void addMedicalRecord(MedicalRecord record) {
        medicalRecords.add(record);
    }

    public String getEntryDate() {
        return ENTRY_DATE;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments =  comments;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) throws IllegalArgumentException {
        if (!gender.matches("(?i)^(male|female|other)$")) {
            throw new IllegalArgumentException("Invalid gender. Acceptable values are male, female, or other.");
        }
        this.gender = gender.toLowerCase(); // Store in a consistent format
    }
     /**
     * Removes all expired water supplies (used one day after allocation).
     */
   
    public void removeExpiredWater() {
        if (personalBelongings == null || personalBelongings.length == 0) {
             return;
        }

        int count = 0;
        for (Supply supply : personalBelongings) {
          if (!(supply instanceof Water && ((Water) supply).isExpired())) {
             count++;
          }
        }

        Supply[] filtered = new Supply[count];
        int i = 0;
        for (Supply supply : personalBelongings) {
          if (!(supply instanceof Water && ((Water) supply).isExpired())) {
             filtered[i++] = supply;
          }
        }

         this.personalBelongings = filtered;
}

}

   






