package org.elastos.hive.controller;

import org.elastos.hive.Backup;
import org.elastos.hive.BackupAuthenticationHandler;
import org.elastos.hive.activites.Activity;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BackupController extends Controller {

	private static BackupController mInstance = null;
	private Backup backup;
	private Activity activity;
	private String targetDid;
	private String targetHost;

	public static BackupController newInstance(Activity activity, String targetDid, String targetHost, Backup backup) {
		if(mInstance == null) {
			mInstance = new BackupController(activity, targetDid, targetHost, backup);
		}

		return mInstance;
	}

	private BackupController(Activity activity, String targetDid, String targetHost, Backup backup) {
		this.activity = activity;
		this.backup = backup;
		this.targetDid = targetDid;
		this.targetHost = targetHost;
	}
	
	@Override
	void execute() {
		getState();
		save();
		restore();
	}


	public void getState() {
		CompletableFuture<Boolean> future = backup.getState()
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
	public void save() {
		BackupAuthenticationHandler handler = new BackupAuthenticationHandler() {
			@Override
			public CompletableFuture<String> getAuthorization(String serviceDid) {
				return CompletableFuture.supplyAsync(() ->
						activity.getBackupVc(serviceDid));
			}

			@Override
			public String getTargetHost() {
				return targetHost;
			}

			@Override
			public String getTargetDid() {
				return targetDid;
			}
		};

		CompletableFuture<Boolean> future = backup.save(handler)
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
	public void restore() {
		BackupAuthenticationHandler handler = new BackupAuthenticationHandler() {
			@Override
			public CompletableFuture<String> getAuthorization(String serviceDid) {
				return CompletableFuture.supplyAsync(() ->
						activity.getBackupVc(serviceDid));
			}

			@Override
			public String getTargetHost() {
				return targetHost;
			}

			@Override
			public String getTargetDid() {
				return targetDid;
			}
		};
		CompletableFuture<Boolean> future = backup.restore(handler)
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
