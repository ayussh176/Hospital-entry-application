package com.example.cep.activities.activties;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hospital.records.R;
import com.hospital.records.adapters.PatientAdapter;
import com.hospital.records.database.MongoDBHelper;
import com.hospital.records.models.Patient;

import io.realm.RealmResults;
import io.realm.Sort;

public class PatientListActivity extends AppCompatActivity implements
        PatientAdapter.OnPatientClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PatientAdapter adapter;
    private MongoDBHelper dbHelper;
    private RealmResults<Patient> patients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        // Initialize database helper
        dbHelper = new MongoDBHelper(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Patient Records");
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        FloatingActionButton fabAddPatient = findViewById(R.id.fabAddPatient);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PatientAdapter(this);
        recyclerView.setAdapter(adapter);

        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this);

        // Set click listeners
        fabAddPatient.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditPatientActivity.class));
        });

        // Load patient data
        loadPatients();
    }

    private void loadPatients() {
        swipeRefreshLayout.setRefreshing(true);

        dbHelper.getAllPatients(new MongoDBHelper.PatientDataCallback() {
            @Override
            public void onSuccess(RealmResults<Patient> result) {
                patients = result.sort("lastName", Sort.ASCENDING);
                adapter.setPatients(patients);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String error) {
                swipeRefreshLayout.setRefreshing(false);
                showError("Failed to load patients: " + error);
            }
        });
    }

    @Override
    public void onPatientClick(Patient patient) {
        Intent intent = new Intent(this, PatientDetailActivity.class);
        intent.putExtra("patient_id", patient.getPatientId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_list_menu, menu);

        // Setup search functionality
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_sort_name) {
            sortPatientsByName();
            return true;
        } else if (id == R.id.action_sort_date) {
            sortPatientsByDate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortPatientsByName() {
        if (patients != null) {
            patients = patients.sort("lastName", Sort.ASCENDING);
            adapter.setPatients(patients);
        }
    }

    private void sortPatientsByDate() {
        if (patients != null) {
            patients = patients.sort("registrationDate", Sort.DESCENDING);
            adapter.setPatients(patients);
        }
    }

    @Override
    public void onRefresh() {
        loadPatients();
    }

    private void showError(String message) {
        // You can replace this with a proper error display (e.g., Snackbar)
        runOnUiThread(() ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
