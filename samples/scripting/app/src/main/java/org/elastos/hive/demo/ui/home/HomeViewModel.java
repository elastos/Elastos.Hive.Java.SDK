package org.elastos.hive.demo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.elastos.hive.demo.MainActivity;
import org.elastos.hive.demo.sdk.SdkContext;
import org.elastos.hive.demo.sdk.scripting.ScriptOwner;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private SdkContext sdkContext;
    private ScriptOwner scriptOwner;
    private MainActivity mainActivity;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.sdkContext = mainActivity.getSdkContext();
        this.scriptOwner = new ScriptOwner(mainActivity.getSdkContext());
    }

    public void setScript() {
        this.mainActivity.loading(true);
        this.scriptOwner.setScript().handleAsync(
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