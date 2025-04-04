package com.example.cep.activities.activties.database;

public void getAllPatients(PatientDataCallback callback) {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<Patient> patients = realm.where(Patient.class).findAll();

    if (patients.isValid()) {
        callback.onSuccess(patients);
    } else {
        callback.onFailure("Invalid patient data");
    }
}

public void getPatientById(String patientId, PatientCallback callback) {
    Realm realm = Realm.getDefaultInstance();
    Patient patient = realm.where(Patient.class)
            .equalTo("patientId", patientId)
            .findFirst();

    if (patient != null && patient.isValid()) {
        callback.onSuccess(patient);
    } else {
        callback.onFailure("Patient not found");
    }
}

public interface PatientCallback {
    void onSuccess(Patient result);
    void onFailure(String error);
}

public interface PatientDataCallback {
    void onSuccess(RealmResults<Patient> result);
    void onFailure(String error);
}
