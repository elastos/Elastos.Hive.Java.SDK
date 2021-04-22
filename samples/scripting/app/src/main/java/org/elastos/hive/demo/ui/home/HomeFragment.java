package org.elastos.hive.demo.ui.home;

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

public class HomeFragment extends Fragment {

    private MainActivity mainActivity;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mainActivity = (MainActivity) getActivity();
        homeViewModel.setMainActivity(mainActivity);
        Button button= (Button)root.findViewById(R.id.owner_set_bttn);
        button.setOnClickListener(view -> {
                homeViewModel.setScript();
        });

        return root;
    }
}