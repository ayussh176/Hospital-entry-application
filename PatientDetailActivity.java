package com.example.cep.activities.activties;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hospital.records.R;
import com.hospital.records.adapters.PatientDetailPagerAdapter;
import com.hospital.records.database.MongoDBHelper;
import com.hospital.records.models.Patient;

import io.realm.Realm;

public class PatientDetailActivity extends AppCompatActivity {

    private String patientId;
    private Patient patient;
    private Realm realm;
    private MongoDBHelper dbHelper;

    private TextView tvPatientName;
    private TextView tvPatientInfo;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        // Get patient ID from intent
        patientId = getIntent().getStringExtra("patient_id");
        if (patientId == null) {
            Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize database
        dbHelper = new MongoDBHelper(this);
        realm = Realm.getDefaultInstance();

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvPatientName = findViewById(R.id.tvPatientName);
        tvPatientInfo = findViewById(R.id.tvPatientInfo);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Load patient data
        loadPatientData();
    }

    private void loadPatientData() {
        dbHelper.getPatientById(patientId, new MongoDBHelper.PatientCallback() {
            @Override
            public void onSuccess(Patient result) {
                patient = result;
                updateUI();
                setupViewPager();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(PatientDetailActivity.this,
                        "Failed to load patient: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI() {
        if (patient != null) {
            String name = patient.getFirstName() + " " + patient.getLastName();
            String info = patient.getGender() + " | " + patient.getAge() + " years | " +
                    patient.getBloodType();

            tvPatientName.setText(name);
            tvPatientInfo.setText(info);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(name);
            }
        }
    }

    private void setupViewPager() {
        PatientDetailPagerAdapter adapter = new PatientDetailPagerAdapter(this, patientId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Overview");
                    break;
                case 1:
                    tab.setText("Records");
                    break;
                case 2:
                    tab.setText("Appointments");
                    break;
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_edit) {
            editPatient();
            return true;
        } else if (id == R.id.action_add_record) {
            addMedicalRecord();
            return true;
        } else if (id == R.id.action_add_appointment) {
            addAppointment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void editPatient() {
        Intent intent = new Intent(this, AddEditPatientActivity.class);
        intent.putExtra("patient_id", patientId);
        startActivity(intent);
    }

    private void addMedicalRecord() {
        Intent intent = new Intent(this, AddMedicalRecordActivity.class);
        intent.putExtra("patient_id", patientId);
        startActivity(intent);
    }

    private void addAppointment() {
        Intent intent = new Intent(this, AddAppointmentActivity.class);
        intent.putExtra("patient_id", patientId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}