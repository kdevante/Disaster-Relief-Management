package edu.ucalgary.oop;

public class Inquirer {
    private final String FIRST_NAME;
    private final String LAST_NAME;
    private final String INFO;
    private final String SERVICES_PHONE;

    public Inquirer(String firstName, String lastName, String phone, String info) {
        this.FIRST_NAME = firstName;
        this.LAST_NAME = lastName;
        this.SERVICES_PHONE = phone;
        this.INFO = info;

    }
    public Inquirer(DisasterVictim disasterVictim, String phone, String info){
        this.FIRST_NAME = disasterVictim.getFirstName();
        this.LAST_NAME = disasterVictim.getLastName();
        this.SERVICES_PHONE = phone;
        this.INFO = info;
        
    }

    public String getFirstName() { return this.FIRST_NAME; }
    public String getLastName() { return this.LAST_NAME; }
    public String getServicesPhoneNum() { return this.SERVICES_PHONE; }
    public String getInfo() { return this.INFO; }
}
