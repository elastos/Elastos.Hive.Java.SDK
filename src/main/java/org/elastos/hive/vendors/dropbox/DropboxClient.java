/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendors.dropbox;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;

import java.util.concurrent.CompletableFuture;

public final class DropboxClient extends Client {
	private static Client clientInstance;
	private Client.Info clientInfo;

	private DropboxClient(DropboxParameter parameter) {
		// TODO;
	}

	public static Client createInstance(DropboxParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new DropboxClient(parameter);
		}
		return clientInstance;
	}

	public static Client getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.dropbox;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		throw new HiveException("Not implemented yet");
	}

	@Override
	public synchronized void logout() throws HiveException {
		throw new HiveException("Not implemented yet");
	}

	@Override
	public Client.Info getLastInfo() {
		return clientInfo;
	}

	@Override
	public CompletableFuture<Client.Info> getInfo() {
		return getInfo(new NullCallback<Client.Info>());
	}

	@Override
	public CompletableFuture<Client.Info> getInfo(Callback<Client.Info> callback) {
		if (callback == null)
			callback = new NullCallback<Client.Info>();

		CompletableFuture<Client.Info> future = new CompletableFuture<Client.Info>();
		HiveException e = new HiveException("Not implemented yet");
		callback.onError(e);
		future.completeExceptionally(e);
		return future;
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive() {
		return  getDefaultDrive(new NullCallback<Drive>());
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback) {
		if (callback == null)
			callback = new NullCallback<Drive>();

		CompletableFuture<Drive> future = new CompletableFuture<Drive>();
		HiveException e = new HiveException("Not implemented yet");
		callback.onError(e);
		future.completeExceptionally(e);
		return future;
	}
}
