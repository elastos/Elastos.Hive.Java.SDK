package org.elastos.hive.demo.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.elastos.hive.demo.MainActivity;
import org.elastos.hive.demo.sdk.SdkContext;
import org.elastos.hive.demo.sdk.scripting.ScriptCaller;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MainActivity mainActivity;
    private SdkContext sdkContext;
    private ScriptCaller scriptCaller;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.sdkContext = mainActivity.getSdkContext();
        this.scriptCaller = new ScriptCaller(mainActivity.getSdkContext());
    }

    public void runScript() {
        this.mainActivity.loading(true);
        this.scriptCaller.runScript().handleAsync(
                (result, e)-> {
                    String msg = e == null ? "Success" : "Failed: " + e.getMessage();
                    mainActivity.runOnUiThread(() -> {
                        // 更新UI的操作
                        mText.setValue(msg);
                        mainActivity.loading(false);
                    });
                    return true;
                });
    }
}