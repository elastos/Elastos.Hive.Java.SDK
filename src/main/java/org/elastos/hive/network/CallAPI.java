package org.elastos.hive.network;

public interface CallAPI
		extends FilesAPI, DatabaseAPI, ScriptingAPI, BackupAPI, PaymentAPI, SubscriptionAPI {

	String API_SCRIPT_UPLOAD = "/scripting/run_script_upload";
	String API_UPLOAD = "/files/upload";
}
