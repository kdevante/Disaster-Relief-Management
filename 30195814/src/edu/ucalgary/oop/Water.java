package edu.ucalgary.oop;

import java.time.LocalDate;

public class Water extends Supply {
    private LocalDate allocationDate;

    public Water(LocalDate allocationDate, int quantity) {
        super("water",quantity);
        this.allocationDate = allocationDate;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }

    public boolean isExpired() {
        return allocationDate != null && LocalDate.now().isAfter(allocationDate.plusDays(1));
    }
}
