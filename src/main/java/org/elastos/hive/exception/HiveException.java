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

package org.elastos.hive.exception;

/**
 * Hive exception
 * If an exception occurs while you are using the hive SDK, throw this HiveException
 */
public class HiveException extends Exception {
	private static final long serialVersionUID = 1486850840770311509L;

	/*
	public static final String RPC_NODE_NULL = "Rpc node is null .";
	public static final String RPC_NODE_PORT_NULL = "Rpc node port is null .";
	public static final String NO_RPC_NODE_AVAILABLE = "No RPC nodes are available .";

	public static final String GET_FILE_LENGTH_ERROR = "Get file length error .";
	public static final String GET_FILE_ERROR = "Get file error .";
	public static final String PUT_FILE_ERROR = "Put file error .";
	public static final String LIST_FILE_ERROR = "List file error .";
	public static final String PUT_BUFFER_ERROR = "Put buffer error .";
	public static final String GET_BUFFER_ERROR = "Get buffer error .";
	public static final String DEL_FILE_ERROR = "Delete file error .";
	public static final String GET_VALUE_ERROR = "Get value error .";


	public static final String FILE_ALREADY_EXIST_ERROR = "File already exist error .";
	public static final String ITEM_NOT_FOUND = "Item not found.";

	public static final String UNSUPPORT_FUNCTION = "Unsupport this function.";

	public static final String CONNECT_ERROR = "Connect error.";
	*/

	public HiveException() {
		super();
	}

	public HiveException(String message) {
		super(message);
	}

	public HiveException(String message, Throwable cause) {
		super(message, cause);
	}

	public HiveException(Throwable cause) {
		super(cause);
	}
}
