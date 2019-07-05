package org.elastos.hive.vendors.onedrive.network.Model;

import java.util.Arrays;

/*
{
    "@odata.context": "https://graph.microsoft.com/v1.0/$metadata#users/$entity",
    "businessPhones": [
        "+1 412 555 0109"
    ],
    "displayName": "Megan Bowen",
    "givenName": "Megan",
    "jobTitle": "Auditor",
    "mail": "MeganB@M365x214355.onmicrosoft.com",
    "mobilePhone": null,
    "officeLocation": "12/1110",
    "preferredLanguage": "en-US",
    "surname": "Bowen",
    "userPrincipalName": "MeganB@M365x214355.onmicrosoft.com",
    "id": "48d31887-5fad-4d73-a9f5-3c356e68a038"
}
 */
public class ClientResponse {
    private String[] businessPhones ;
    private String displayName ;
    private String givenName ;
    private String jobTitle ;
    private String mail ;
    private String mobilePhone ;
    private String officeLocation ;
    private String preferredLanguage ;
    private String surname ;
    private String userPrincipalName ;
    private String id ;

    public ClientResponse(String[] businessPhones, String displayName, String givenName, String jobTitle, String mail, String mobilePhone, String officeLocation, String preferredLanguage, String surname, String userPrincipalName, String id) {
        this.businessPhones = businessPhones;
        this.displayName = displayName;
        this.givenName = givenName;
        this.jobTitle = jobTitle;
        this.mail = mail;
        this.mobilePhone = mobilePhone;
        this.officeLocation = officeLocation;
        this.preferredLanguage = preferredLanguage;
        this.surname = surname;
        this.userPrincipalName = userPrincipalName;
        this.id = id;
    }

    public String[] getBusinessPhones() {
        return businessPhones;
    }

    public void setBusinessPhones(String[] businessPhones) {
        this.businessPhones = businessPhones;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ClientResponse{" +
                "businessPhones=" + Arrays.toString(businessPhones) +
                ", displayName='" + displayName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", mail='" + mail + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", officeLocation='" + officeLocation + '\'' +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", surname='" + surname + '\'' +
                ", userPrincipalName='" + userPrincipalName + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
