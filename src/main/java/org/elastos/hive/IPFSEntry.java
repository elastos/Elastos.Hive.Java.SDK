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

/**
 * Elastos Hive SDK configuration for IPFS.<br>
 * All app information should match the information in your app portal
 */
public class IPFSEntry {
	private String uid;
	private final String[] rpcAddrs;

	/**
	 * IPFSEntry Constructor
	 * @param uid User uid
	 * @param rpcAddrs IPFS rpc address list
	 */
	public IPFSEntry(String uid, String[] rpcAddrs) {
		this.uid = uid;
		this.rpcAddrs = rpcAddrs;
	}

	/**
	 * Set the unique uid.
	 * @param uid User uid .The unique uid for current user.
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Get current unique uid.
	 * @return Current unique uid.
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Get IPFS rpc address list .
	 * @return User IPFS rpc address list .
	 */
	public String[] getRcpAddrs() {
		return rpcAddrs;
	}
}
