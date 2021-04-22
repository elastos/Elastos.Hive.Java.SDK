package org.elastos.hive.demo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.elastos.hive.demo.sdk.SdkContext;
import org.elastos.hive.demo.sdk.Utils;
import org.elastos.hive.demo.sdk.scripting.ScriptOwner;

import java.util.concurrent.ExecutionException;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private SdkContext sdkContext;
    private ScriptOwner scriptOwner;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setSdkContext(SdkContext sdkContext) {
        this.sdkContext = sdkContext;
        this.scriptOwner = new ScriptOwner(sdkContext);
    }

    public void setScript() {
        try {
            boolean isSuccess = this.scriptOwner.setScript();
            mText.setValue(isSuccess ? "Successfully register script" : "Failed to register script");
        } catch (ExecutionException e) {
            mText.setValue(Utils.getExceptionTrace(e.getCause()));
        } catch (InterruptedException e) {
            mText.setValue("Interrupted when registering script.");
        }
    }
}