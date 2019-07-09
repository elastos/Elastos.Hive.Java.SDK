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

package org.elastos.hive;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public abstract class File extends Result implements ResourceItem<File.Info>, FileItem {
	public static class Info extends AttributeMap {
		public static final String itemId = "ItemId";
		public static final String name   = "Name";
		public static final String size   = "Size";

		public Info(HashMap<String, String> hash) {
			super(hash);
		}
	}

	public abstract CompletableFuture<Length> read(ByteBuffer dest);
	public abstract CompletableFuture<Length> read(ByteBuffer dest, Callback<Length> callback);

	public abstract CompletableFuture<Length> read(ByteBuffer dest, long position);
	public abstract CompletableFuture<Length> read(ByteBuffer dest, long position, Callback<Length> callback);

	public abstract CompletableFuture<Length> write(ByteBuffer dest);
	public abstract CompletableFuture<Length> write(ByteBuffer dest, Callback<Length> callback);

	public abstract CompletableFuture<Length> write(ByteBuffer dest, long position);
	public abstract CompletableFuture<Length> write(ByteBuffer dest, long position, Callback<Length> callback);

	public abstract CompletableFuture<Void> commit();
	public abstract CompletableFuture<Void> commit(Callback<Void> callback);

	public abstract void discard();
}
