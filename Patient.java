package com.example.cep.activities.activties.models;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Patient extends RealmObject {
    @PrimaryKey
    private ObjectId _id;
    @Required
    private String patientId;
    @Required
    private String firstName;
    @Required
    private String lastName;
    private String gender;
    private String phoneNumber;
    private long registrationDate;

    // Getters and setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    // ... other getters/setters
}