package org.elastos.hive;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AuthHelper {

	public AuthHelper(AppContext appContext) {

	}

	public CompletableFuture<Void> login() {
		return CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {

			}
		});
	}

	private CompletableFuture<String> challenge() {
		return CompletableFuture.supplyAsync(new Supplier<String>() {
			@Override
			public String get() {
				return null;
			}
		});
	}

	private CompletableFuture<Void> authorize() {
		return CompletableFuture.supplyAsync(new Supplier<Void>() {
			@Override
			public Void get() {
				return null;
			}
		});
	}

}
