package org.elastos.hive.demo.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.elastos.hive.demo.MainActivity;
import org.elastos.hive.demo.R;
import org.elastos.hive.demo.UpdateUserDidDialog;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        homeViewModel.setMainActivity(mainActivity);

        // bind text view to model data.
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        // listen button to update user did.
        Button button= (Button)root.findViewById(R.id.owner_set_did);
        button.setOnClickListener(view -> {
            UpdateUserDidDialog dialog = new UpdateUserDidDialog(mainActivity);
            dialog.show();
        });

        // listen button to upload file.
        button= (Button)root.findViewById(R.id.owner_upload_file);
        button.setOnClickListener(view -> {
            homeViewModel.uploadFile();
        });

        // listen button to set script.

        button= (Button)root.findViewById(R.id.owner_set_script);
        button.setOnClickListener(view -> {
            homeViewModel.setScript();
        });

        return root;
    }
}