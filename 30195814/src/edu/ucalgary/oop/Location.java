package edu.ucalgary.oop;

import java.util.ArrayList;

/**
 * Represents a physical location where disaster victims can be placed and supplies can be stored.
 * Supplies can only be stored at locations unless allocated to individuals by DisasterVictim logic.
 * 
 * @author Devante Kwizera
 * @version 1.0
 * @since 2025-04-09
 */
public class Location {
    private String name;
    private String address;
    private ArrayList<DisasterVictim> occupants;
    private ArrayList<Supply> supplies;

    /**
     * Constructs a Location with a name and address.
     * 
     * @param name    the name of the location
     * @param address the physical address of the location
     */
    public Location(String name, String address) {
        this.name = name;
        this.address = address;
        this.occupants = new ArrayList<>();
        this.supplies = new ArrayList<>();
    }

    // ---------- Getters and Setters ---------- //

    /**
     * @return the name of this location
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the new name of this location
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the address of this location
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the new address of this location
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return a list of current occupants
     */
    public ArrayList<DisasterVictim> getOccupants() {
        return new ArrayList<>(occupants);
    }

    /**
     * @return a list of supplies at this location
     */
    public ArrayList<Supply> getSupplies() {
        return new ArrayList<>(supplies);
    }

    // ---------- Core Logic ---------- //

    /**
     * Adds an occupant to this location.
     * 
     * @param occupant the DisasterVictim to add
     */
    public void addOccupant(DisasterVictim occupant) {
        if (!occupants.contains(occupant)) {
            occupants.add(occupant);
        }
    }

    /**
     * Removes an occupant from this location.
     * 
     * @param occupant the DisasterVictim to remove
     */
    public void removeOccupant(DisasterVictim occupant) {
        occupants.remove(occupant);
    }

    /**
     * Adds a supply to this location. Personal belongings cannot be added.
     * 
     * @param supply the supply item to store
     * @throws IllegalArgumentException if the supply is a personal belonging
     */
    public void addSupply(Supply supply) {
        if (supply instanceof PersonalBelonging) {
            throw new IllegalArgumentException("Personal belongings cannot be added to a location.");
        }
        supplies.add(supply);
    }

    /**
     * Removes a supply from this location.
     * 
     * @param supply the supply to remove
     */
    public void removeSupply(Supply supply) {
        supplies.remove(supply);
    }
}
