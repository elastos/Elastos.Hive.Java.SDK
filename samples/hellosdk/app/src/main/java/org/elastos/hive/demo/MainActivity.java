package org.elastos.hive.demo;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.elastos.did.exception.DIDException;
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

        showToastMessage("hello world application.");
    }

    private void initSdkContext() {
        assetManager = getAssets();
        try {
            sdkContext = SdkContext.getInstance(this);
        } catch (Exception e) {
            showToastMessage("Failed to initialize hive sdk.");
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

    public void showToastMessage(String msg) {
        Log.d("Toast: ", msg);
        runOnUiThread(() -> Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show());
    }

    public void updateUserDid(String mnemonic, String passPhrase) {
        try {
            getSdkContext().updateUserDid(mnemonic, passPhrase);
            showToastMessage("Success to update user did.");
        } catch (DIDException e) {
            showToastMessage("Failed to update user did.");
        }
    }
}
