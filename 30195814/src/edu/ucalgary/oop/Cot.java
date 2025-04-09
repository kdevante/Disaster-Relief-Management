package edu.ucalgary.oop;

public class Cot extends Supply {
    private int room;
    private String grid;

    public Cot(int room, String grid, int quantity) {
        super("cot", quantity);
        this.room = room;
        this.grid = grid;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public String getGrid() {
        return grid;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }
}
