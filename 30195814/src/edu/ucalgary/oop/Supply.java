package edu.ucalgary.oop;

/**
 * Abstract class representing a generic supply item.
 * Specific item types like Water, Cot, etc. should extend this class.
 */ 

public class Supply {
    private String type;
    private int quantity;

    public Supply(String type, int quantity) {
        this.type = type;
        this.quantity = quantity;
    }

    public void setType(String type) { this.type = type; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getType() { return this.type; }
    public int getQuantity() { return this.quantity; }
}
