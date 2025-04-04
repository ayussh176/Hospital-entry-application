package com.example.cep.activities.activties;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.hospital.records.R;
import com.hospital.records.database.MongoDBHelper;
import com.hospital.records.models.Doctor;

import io.realm.mongodb.User;

public class MainActivity extends AppCompatActivity {

    private MongoDBHelper dbHelper;
    private TextView tvWelcome;
    private CardView cvPatients, cvAppointments, cvRecords, cvReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        dbHelper = new MongoDBHelper(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        cvPatients = findViewById(R.id.cvPatients);
        cvAppointments = findViewById(R.id.cvAppointments);
        cvRecords = findViewById(R.id.cvRecords);
        cvReports = findViewById(R.id.cvReports);

        // Load doctor profile
        loadDoctorProfile();

        // Set click listeners for dashboard cards
        cvPatients.setOnClickListener(v -> openPatientManagement());
        cvAppointments.setOnClickListener(v -> openAppointmentManagement());
        cvRecords.setOnClickListener(v -> openMedicalRecords());
        cvReports.setOnClickListener(v -> openReports());
    }

    private void loadDoctorProfile() {
        User user = dbHelper.getCurrentUser();
        if (user != null) {
            dbHelper.getDoctorProfile(user.getId(), new MongoDBHelper.DoctorProfileCallback() {
                @Override
                public void onSuccess(Doctor doctor) {
                    String welcomeText = "Welcome, Dr. " + doctor.getLastName();
                    tvWelcome.setText(welcomeText);
                }

                @Override
                public void onFailure(String error) {
                    tvWelcome.setText("Welcome, Doctor");
                    Toast.makeText(MainActivity.this,
                            "Couldn't load profile: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openPatientManagement() {
        startActivity(new Intent(this, PatientListActivity.class));
    }

    private void openAppointmentManagement() {
        startActivity(new Intent(this, AppointmentActivity.class));
    }

    private void openMedicalRecords() {
        startActivity(new Intent(this, RecordSearchActivity.class));
    }

    private void openReports() {
        startActivity(new Intent(this, ReportsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        dbHelper.logoutUser(new MongoDBHelper.LogoutCallback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this,
                        "Logout failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}