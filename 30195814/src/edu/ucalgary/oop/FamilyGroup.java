package edu.ucalgary.oop;

import java.util.HashSet;
import java.util.Set;

public class FamilyGroup {
    private String groupId;
    private Set<DisasterVictim> members;

    public FamilyGroup(String groupID) {
        this.groupId = groupID;
        this.members = new HashSet<>();
    }

    public String getGroupId() {
        return groupId;
    }

    public Set<DisasterVictim> getMembers() {
        return members;
    }

    public void addMember(DisasterVictim person) {
        if (person.getFamilyGroup() != null) {
            throw new IllegalArgumentException("This person is already part of a family group.");
        }
        members.add(person);
        person.setFamilyGroup(this);
    }

    public void removeMember(DisasterVictim person) {
        if (members.remove(person)) {
            person.setFamilyGroup(null);
        }
    }

    public int getSize() {
        return members.size();
    }
}
