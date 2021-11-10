package org.elastos.hive.demo.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.elastos.hive.demo.MainActivity;
import org.elastos.hive.demo.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // listen button to run script.
        mainActivity = (MainActivity) getActivity();
        dashboardViewModel.setMainActivity(mainActivity);
        Button button= (Button)root.findViewById(R.id.caller_run_bttn);
        button.setOnClickListener(view -> {
            dashboardViewModel.runScript();
        });

        return root;
    }
}