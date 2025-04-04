package com.example.cep.activities.activties.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hospital.records.fragments.PatientAppointmentsFragment;
import com.hospital.records.fragments.PatientOverviewFragment;
import com.hospital.records.fragments.PatientRecordsFragment;

public class PatientDetailPagerAdapter extends FragmentStateAdapter {

    private final String patientId;

    public PatientDetailPagerAdapter(FragmentActivity fa, String patientId) {
        super(fa);
        this.patientId = patientId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PatientOverviewFragment.newInstance(patientId);
            case 1:
                return PatientRecordsFragment.newInstance(patientId);
            case 2:
                return PatientAppointmentsFragment.newInstance(patientId);
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}