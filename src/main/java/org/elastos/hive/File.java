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

/**
 * Hive File<br>
 * <br>
 * Create Hive File instance,<br>
 * <br>
 * for example:
 * <p>
 *     First of all , create Hive {@link Client},<br>
 *     Second , create the Hive {@link Drive} from the Hive {@link Client},<br>
 *     Third , create the Hive {@link File} from the Hive {@link Drive}.
 * </p>
 * or
 * <p>
 *     First of all , create Hive {@link Client},<br>
 *     Second , create the Hive {@link Drive} from the Hive {@link Client},<br>
 *     Third , create the Hive {@link Directory} from the Hive {@link Drive},<br>
 *     Fourth , create the Hive {@link File} from the Hive {@link Directory}.
 * </p>
 *
 */
public abstract class File extends Result implements ResourceItem<File.Info>, FileItem {
	/**
	 * Representing the hive file information.
	 */
	public static class Info extends AttributeMap {
		/**
		 * File Id
		 */
		public static final String itemId = "ItemId";

		/**
		 * File name
		 */
		public static final String name   = "Name";

		/**
		 * File size
		 */
		public static final String size   = "Size";

		/**
		 * Hive file info constructor
		 * @param hash hive file information.
		 */
		public Info(HashMap<String, String> hash) {
			super(hash);
		}
	}

	/**
	 * Read ByteBuffer data from file.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".<br>
	 * <br>
	 * @param dest ByteBuffer data from {@link File}.
	 * @return Return {@link Length}<br>
	 * If no error occurs, return of data actually read and fill into {@link Length#length}.<br>
	 * Otherwise, return -1 and fill into {@link Length#length}
	 */
	public abstract CompletableFuture<Length> read(ByteBuffer dest);

	/**
	 * Read ByteBuffer data from file.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".<br>
	 * <br>
	 * @param dest ByteBuffer data from file.
	 * @param callback Callback result.Reference return result.
	 * @return Return {@link Length}<br>
	 * If no error occurs, return of data actually read and fill into {@link Length#length}.<br>
	 * Otherwise, return -1 and fill into {@link Length#length}
	 */
	public abstract CompletableFuture<Length> read(ByteBuffer dest, Callback<Length> callback);

	/**
	 * Reads the ByteBuffer from the location specified in the file.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".<br>
	 * <br>
	 * @param dest ByteBuffer data from file.
	 * @param position location specified in the file.
	 * @return Return {@link Length}
	 * If no error occurs, return of data actually read and fill into {@link Length#length}.<br>
	 * Otherwise, return -1 and fill into {@link Length#length}
	 */
	public abstract CompletableFuture<Length> read(ByteBuffer dest, long position);

	/**
	 * Reads the ByteBuffer from the location specified in the file.<br>
	 * And callback result from {@link Callback} param.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".<br>
	 * <br>
	 * @param dest ByteBuffer data from file.
	 * @param position location specified in the file.
	 * @param callback  Callback result. Reference return result.
	 * @return Return {@link Length}
	 * If no error occurs, return of data actually read and fill into {@link Length#length}.<br>
	 * Otherwise, return -1 and fill into {@link Length#length}
	 */
	public abstract CompletableFuture<Length> read(ByteBuffer dest, long position, Callback<Length> callback);

	/**
	 * Write ByteBuffer bytes of data to file .<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".<br>
	 *
	 * @param dest ByteBuffer bytes of data .
	 * @return Return {@link Length}
	 * If no error occurs, return of data actually written and fill into {@link Length#length}.<br>
	 * Otherwise, return -1 and fill into {@link Length#length}
	 */
	public abstract CompletableFuture<Length> write(ByteBuffer dest);

	/**
	 * Write ByteBuffer bytes of data to file .<br>
	 * And callback result from {@link Callback} param.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".<br>
	 * <br>
	 * @param dest ByteBuffer bytes of data .
	 * @param callback Callback result. Reference return result.
	 * @return Return {@link Length}
	 * If no error occurs, return of data actually written and fill into {@link Length#length}.
	 * Otherwise, return -1 and fill into {@link Length#length}
	 */
	public abstract CompletableFuture<Length> write(ByteBuffer dest, Callback<Length> callback);

	/**
	 * Write the ByteBuffer from the location specified in the file.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".<br>
	 * <br>
	 * @param dest ByteBuffer bytes of data .
	 * @param position ByteBuffer from the location specified in the file
	 * @return Return {@link Length}
	 * If no error occurs, return of data actually written and fill into {@link Length#length}.<br>
	 * Otherwise, return -1 and fill into {@link Length#length}
	 */
	public abstract CompletableFuture<Length> write(ByteBuffer dest, long position);

	/**
	 * Write the ByteBuffer from the location specified in the file.<br>
	 * And callback result from {@link Callback} param.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".<br>
	 * <br>
	 * @param dest ByteBuffer bytes of data .
	 * @param position ByteBuffer from the location specified in the file
	 * @param callback Callback result. Reference return result.
	 * @return Return {@link Length}
	 * If no error occurs, return of data actually written and fill into {@link Length#length}.<br>
	 * Otherwise, return -1 and fill into {@link Length#length}
	 */
	public abstract CompletableFuture<Length> write(ByteBuffer dest, long position, Callback<Length> callback);

	/**
	 * Commit local change on {@link File} to backend.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".
	 * <br>
	 * @return Return {@link Void}
	 */
	public abstract CompletableFuture<Void> commit();

	/**
	 * Commit local change on {@link File} to backend.<br>
	 * And callback result from {@link Callback} param.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".
	 * <br>
	 * @param callback Callback result.
	 * @return Return {@link Void}
	 */
	public abstract CompletableFuture<Void> commit(Callback<Void> callback);

	/**
	 * Discard local change on {@link File}.<br>
	 * <br>
	 * This function is effective only when state of client associated with {@link File} is "logined".
	 */
	public abstract void discard();
}
