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
 * {@link Directory} Action
 * {@link Drive} or {@link Directory} implements this interface
 */
public interface DirectoryItem {
	/**
	 * Create directory in current {@link DriveType}, and return this directory instance.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @param path Directory path
	 * @return Return {@link Directory} instance
	 * Sync method
	 */
	CompletableFuture<Directory> createDirectory(String path);

	/**
	 * Create directory in current {@link DriveType}, and return this directory instance.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @param path Directory path
	 * @param callback callback createDirectory result
	 * @return Return {@link Directory} instance
	 */
	CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback);

	/**
	 * Get current directory instance.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * If the directory does not exist, an exception is thrown
	 * @param path Directory path
	 * @return Return {@link Directory} instance
	 */
	CompletableFuture<Directory> getDirectory(String path);

	/**
	 * Get current directory instance.<br>
	 * If the directory does not exist, an exception is thrown<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @param path Directory path
	 * @param callback Callback getDirectory result
	 * @return Return {@link Directory} instance
	 */
	CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback);

	/**
	 * Create file in current {@link DriveType}, and return this File instance.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @param path File path
	 * @return Return {@link File} instance
	 */
	CompletableFuture<File> createFile(String path);

	/**
	 * Create file in current {@link DriveType}, and return this File instance.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @param path File path
	 * @param callback Callback createFile result
	 * @return Return {@link File} instance
	 */
	CompletableFuture<File> createFile(String path, Callback<File> callback);

	/**
	 * Get current file instance.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @param path File path
	 * @return Return {@link File} instance
	 */
	CompletableFuture<File> getFile(String path);

	/**
	 * Get current file instance.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @param path File path
	 * @param callback Callback getFile result
	 * @return Return {@link File} instance
	 */
	CompletableFuture<File> getFile(String path, Callback<File> callback);
}
