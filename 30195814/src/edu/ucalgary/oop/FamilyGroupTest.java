package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class FamilyGroupTest {
    private FamilyGroup familyGroup;
    private DisasterVictim member1;
    private DisasterVictim member2;
    private String groupId = "Smith";
    
    @Before
    public void setUp() {
        familyGroup = new FamilyGroup(groupId);
        member1 = new DisasterVictim("John Smith", "2025-01-19");
        member2 = new DisasterVictim("Jane Smith", "2025-01-20");
    }
    
    @Test
    public void testObjectCreation() {
        assertNotNull("FamilyGroup should be created successfully", familyGroup);
        assertEquals("FamilyGroup ID should be set correctly", groupId, familyGroup.getGroupId());
    }
    
    @Test
    public void testGetGroupId() {
        assertEquals("getGroupId should return the correct ID", groupId, familyGroup.getGroupId());
    }
    
    @Test
    public void testSetGroupId() {
        String newGroupId = "FG002";
        familyGroup.setGroupId(newGroupId);
        assertEquals("setGroupId should update the group ID", newGroupId, familyGroup.getGroupId());
    }
    
    
    @Test
    public void testAddMember() {
        familyGroup.addMember(member1);
        
        // Check that member is in the group's member list
        assertTrue("Member should be added to the family group", familyGroup.getMembers().contains(member1));
        
        // Check that the member's family group is set correctly
        assertEquals("Member's family group should be set", familyGroup, member1.getFamilyGroup());
    }
    
    @Test
    public void testRemoveMember() {
        // First add a member
        familyGroup.addMember(member1);
        
        // Then remove them
        familyGroup.removeMember(member1);
        
        // Check that member is removed from the group
        assertFalse("Member should be removed from the family group", familyGroup.getMembers().contains(member1));
        
        // Check that the member's family group is null
        assertNull("Member's family group should be null", member1.getFamilyGroup());
    }
    
    @Test
    public void testGetMembers() {
        // Add members to the group
        familyGroup.addMember(member1);
        familyGroup.addMember(member2);
        
        ArrayList<DisasterVictim> members = familyGroup.getMembers();
        
        // Check that the list contains both members
        assertEquals("Members list should have 2 members", 2, members.size());
        assertTrue("Members list should contain member1", members.contains(member1));
        assertTrue("Members list should contain member2", members.contains(member2));
    }
    
    @Test
    public void testMemberCanOnlyBelongToOneGroup() {
        // Create a second family group
        FamilyGroup anotherGroup = new FamilyGroup("Another Family");
        
        // Add member1 to the first group
        familyGroup.addMember(member1);
        assertEquals("Member should belong to first family group", familyGroup, member1.getFamilyGroup());
        
        // Add member1 to the second group
        anotherGroup.addMember(member1);
        
        // Check that member1 is no longer in the first group
        assertFalse("Member should be removed from first family group", familyGroup.getMembers().contains(member1));
        
        // Check that member1 is now in the second group
        assertEquals("Member should belong to second family group", anotherGroup, member1.getFamilyGroup());
        assertTrue("Member should be in second family group's member list", anotherGroup.getMembers().contains(member1));
    }
}