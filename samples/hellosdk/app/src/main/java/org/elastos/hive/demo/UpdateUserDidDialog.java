package org.elastos.hive.demo;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class UpdateUserDidDialog extends Dialog implements
        android.view.View.OnClickListener {

    private MainActivity mainActivity;

    public UpdateUserDidDialog(@NonNull MainActivity activity) {
        super(activity);
        this.mainActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_user_did_dialog);
        final Button okButton = (Button) findViewById(R.id.ok_button);
        final Button cancelButton = (Button) findViewById(R.id.cancel_button);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                TextView m = (TextView) findViewById(R.id.mnemonic);
                TextView p = (TextView) findViewById(R.id.passpharse);
                String mStr = m.getText().toString();
                String pStr = p.getText().toString();
                if (!TextUtils.isEmpty(mStr)) {
                    this.mainActivity.updateUserDid(mStr, pStr);
                }
                dismiss();
                break;
            case R.id.cancel_button:
                dismiss();
                break;
            default:
                break;
        }
    }

}
