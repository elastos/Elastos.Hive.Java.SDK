package org.elastos.hive.demo;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.elastos.did.exception.NetworkException;
import org.elastos.hive.demo.sdk.SdkContext;
import org.elastos.hive.exception.HiveException;

public class MainActivity extends AppCompatActivity {
    private AssetManager assetManager;
    private SdkContext sdkContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableNetworkInMainThread();
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
            throw new RuntimeException("Failed to initSdkContext: " + e.toString());
        }
    }

    public void enableNetworkInMainThread() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public SdkContext getSdkContext() {
        return sdkContext;
    }

    public void loading(boolean isVisible) {
        findViewById(R.id.loadingPanel).setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
