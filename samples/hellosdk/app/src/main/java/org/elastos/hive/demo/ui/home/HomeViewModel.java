package org.elastos.hive.demo.ui.home;

import android.support.v4.os.IResultReceiver;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.elastos.hive.VaultSubscription;
import org.elastos.hive.demo.MainActivity;
import org.elastos.hive.demo.sdk.SdkContext;
import org.elastos.hive.demo.sdk.scripting.ScriptOwner;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class HomeViewModel extends ViewModel {

    private static final String REMOTE_FILE_PATH = "test01.txt";
    private static final String REMOTE_FILE_CONTENT = "File content of the file test01.txt";

    private MainActivity mainActivity;
    private MutableLiveData<String> mText;

    private VaultSubscription vaultSubscription;
    private FilesService filesService;
    private DatabaseService databaseService;
    private ScriptOwner scriptOwner;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        try {
            this.vaultSubscription = mainActivity.getSdkContext().newVaultSubscription();
        } catch (HiveException e) {
            Log.e("HomeViewModel", "Failed to initialize the vault subscription object.");
        }
        this.filesService = mainActivity.getSdkContext().newVault().getFilesService();
        this.scriptOwner = new ScriptOwner(mainActivity.getSdkContext());
    }

    public void subscribeVault() {
        this.showLoading();
        this.vaultSubscription.subscribe()
                .thenAcceptAsync(result -> hideLoadingWithMessage(null))
                .exceptionally(ex -> {
                    hideLoadingWithMessage(ex);
                    return null;
                });
    }

    public CompletableFuture<Void> writeFileContent(Writer writer) {
        return CompletableFuture.runAsync(() -> {
            try {
                writer.write(REMOTE_FILE_CONTENT);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    public void uploadFile() {
        this.showLoading();
        filesService.getUploadWriter(REMOTE_FILE_PATH)
                .thenCompose(this::writeFileContent)
                .thenAcceptAsync(result -> hideLoadingWithMessage(null))
                .exceptionally(ex -> {
                    hideLoadingWithMessage(ex);
                    return null;
                });
    }

    public void setScript() {
        this.showLoading();
        this.scriptOwner.setScript()
                .thenAcceptAsync(result -> hideLoadingWithMessage(null))
                .exceptionally(ex -> {
                    hideLoadingWithMessage(ex);
                    return null;
                });
    }

    public void insertDocument() {
        this.showLoading();
        this.scriptOwner.insertDocument()
                .thenAcceptAsync(result -> hideLoadingWithMessage(null))
                .exceptionally(ex -> {
                    hideLoadingWithMessage(ex);
                    return null;
                });
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
