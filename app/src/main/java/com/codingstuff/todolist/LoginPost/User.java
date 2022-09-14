package com.codingstuff.todolist.LoginPost;

public class User {
    private String displayName, contactNumber, community, privilege;

    public User(String userID, String displayName, String contactNumber, String community, String privilege) {
        this.displayName = displayName;
        this.contactNumber = contactNumber;
        this.community = community;
        this.privilege = privilege;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
}
