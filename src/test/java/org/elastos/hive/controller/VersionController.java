package org.elastos.hive.controller;

import org.elastos.hive.SdkVersion;
import org.elastos.hive.Vault;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class VersionController extends Controller {

	private static VersionController mInstance = null;
	private Vault vault;

	public static VersionController newInstance(Vault vault) {
		if(mInstance == null) {
			mInstance = new VersionController(vault);
		}

		return mInstance;
	}

	private VersionController(Vault vault) {
		this.vault = vault;
	}
	
	@Override
	void execute() {
		try {
			getNodeVersion();
			getNodeLastCommitId();
			getSdkVersion();
		} catch (ExecutionException|InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void getNodeVersion() throws ExecutionException, InterruptedException {
		vault.getNodeVersion().whenComplete((s, throwable) -> {
			assertNull(throwable);
			assertNotNull(s);
			System.out.println("nodeVersion:"+s);
		}).get();
	}

	
	private void getNodeLastCommitId() throws ExecutionException, InterruptedException {
		vault.getNodeLastCommitId().whenComplete((s, throwable) -> {
			assertNull(throwable);
			assertNotNull(s);
			System.out.println("nodeVersion:"+s);
		}).get();
	}

	
	private void getSdkVersion(){
		String version = SdkVersion.getVersion();
		assertNotNull(version);
	}
}
