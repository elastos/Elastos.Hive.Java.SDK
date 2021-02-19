package org.elastos.hive.controller;

import org.elastos.hive.Management;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ManagementController extends Controller {

	private static ManagementController mInstance = null;
	private Management management;

	public static ManagementController newInstance(Management management) {
		if(mInstance == null) {
			mInstance = new ManagementController(management);
		}

		return mInstance;
	}

	private ManagementController(Management management) {
		this.management = management;
	}

	@Override
	void execute() {
		createVault();
		createBackup();
		getVaultServiceInfo();
		getBackupServiceInfo();
	}

	public void createVault() {
		CompletableFuture<Boolean> future = this.management.createVault()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

		try {
			future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	
	public void createBackup() {
		CompletableFuture<Boolean> future = this.management.createBackup()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

		try {
			future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	
	public void destroyVault() {
		CompletableFuture<Boolean> future = this.management.destroyVault()
				.handle((vault, throwable) -> (null == throwable));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
	public void freezeVault() {
		CompletableFuture<Boolean> future = this.management.freezeVault()
				.handle((vault, throwable) -> (null == throwable));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
	public void unfreezeVault() {
		CompletableFuture<Boolean> future = this.management.unfreezeVault()
				.handle((vault, throwable) -> (null == throwable));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
	public void getVaultServiceInfo() {
		CompletableFuture<Boolean> future = this.management.getVaultServiceInfo()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
	public void getBackupServiceInfo() {
		CompletableFuture<Boolean> future = this.management.getBackupServiceInfo()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

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
