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

    public void uploadFile() {
        // TODO:
        mainActivity.showToastMessage("updateFile");
    }

    public void setScript() {
        this.showLoading();
        this.scriptOwner.setScript().whenCompleteAsync((result, e)-> hideLoadingWithMessage(e));
    }

    private void showLoading() {
        this.mainActivity.loading(true);
    }

    private void hideLoadingWithMessage(Throwable e) {
        String msg = e == null ? "Success" : "Failed: " + e.getMessage();
        mainActivity.runOnUiThread(() -> {
            // 更新UI的操作
            mText.setValue(msg);
            mainActivity.loading(false);
        });
    }

}
