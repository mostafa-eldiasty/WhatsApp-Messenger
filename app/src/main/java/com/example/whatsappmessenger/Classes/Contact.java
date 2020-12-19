package com.example.whatsappmessenger.Classes;

public class Contact{
    String contactNames;
    String contactAbout;
    String contactImage;
    String contactPhoneNumber;

    public Contact(String contactNames, String contactAbout, String contactImage, String contactPhoneNumber) {
        this.contactNames = contactNames;
        this.contactAbout = contactAbout;
        this.contactImage = contactImage;
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactNames() {
        return contactNames;
    }

    public void setContactNames(String contactNames) {
        this.contactNames = contactNames;
    }

    public String getContactAbout() {
        return contactAbout;
    }

    public void setContactAbout(String contactAbout) {
        this.contactAbout = contactAbout;
    }

    public String getContactImage() {
        return contactImage;
    }

    public void setContactImage(String contactImage) {
        this.contactImage = contactImage;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }
}