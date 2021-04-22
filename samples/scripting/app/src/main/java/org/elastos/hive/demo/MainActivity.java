package org.elastos.hive.demo;

import android.content.res.AssetManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.elastos.hive.demo.sdk.SdkContext;

public class MainActivity extends AppCompatActivity {
    private AssetManager assetManager;
    private SdkContext sdkContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSdkContext();
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void initSdkContext() {
        assetManager = getAssets();
        try {
            sdkContext = SdkContext.getInstance(assetManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SdkContext getSdkContext() {
        return sdkContext;
    }
}