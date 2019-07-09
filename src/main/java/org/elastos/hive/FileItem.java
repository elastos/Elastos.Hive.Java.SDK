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

import java.util.concurrent.CompletableFuture;

/**
 * {@link File} Action.
 */
interface FileItem {
	/**
	 * Get current file path in backend.
	 * @return current file path in backend.
	 */
	String getPath();

	/**
	 * Get current file parent path in backend.
	 * @return current file parent path in backend.
	 */
	String getParentPath();

	/**
	 * Move the file specified by current path to destination path.<br>
	 * <br>
	 * This function is effective only when state of client generating is "logined".<br>
	 * <br>
	 * @param path The absolute path that a file would move to.
	 * @return Return {@link Void}
	 */
	CompletableFuture<Void> moveTo(String path);

	/**
	 * Move the file specified by current path to destination path.<br>
	 * <br>
	 * This function is effective only when state of client generating is "logined".<br>
	 * <br>
	 * @param path The absolute path that a file would move to.
	 * @param callback Callback moveTo result
	 * @return Return {@link Void}
	 */
	CompletableFuture<Void> moveTo(String path, Callback<Void> callback);

	/**
	 * Copy the file specified by current path to destination path.<br>
	 * <br>
	 * This function is effective only when state of client generating is "logined".<br>
	 * <br>
	 * @param path The absolute path that a file would Copy to.
	 * @return Return {@link Void}
	 */
	CompletableFuture<Void> copyTo(String path);

	/**
	 * Copy the file specified by current path to destination path.<br>
	 * <br>
	 * This function is effective only when state of client generating is "logined".<br>
	 * <br>
	 * @param path The absolute path that a file would Copy to.
	 * @param callback Callback copyTo result
	 * @return Return {@link Void}
	 */
	CompletableFuture<Void> copyTo(String path, Callback<Void> callback);

	/**
	 * Delete the file current file.<br>
	 * <br>
	 * This function is effective only when state of client generating is "logined".<br>
	 * <br>
	 * @return Return {@link Void}
	 */
	CompletableFuture<Void> deleteItem();

	/**
	 * Delete the file current file.<br>
	 * <br>
	 * This function is effective only when state of client generating is "logined".<br>
	 * <br>
	 * @param callback Callback deleteItem result
	 * @return Return {@link Void}
	 */
	CompletableFuture<Void> deleteItem(Callback<Void> callback);
}
