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

package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.DriveType;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.Parameter;

/**
 * Hive sdk IPFS parameter<br>
 * If you want to create an IPFS client, you need to create an IPFS parameter first
 */
public class IPFSParameter implements Parameter<IPFSEntry> {
	private final IPFSEntry entry;
	private final String keyStorePath;


	/**
	 * IPFSParameter Constructor
	 * @param entry IPFS entry {@link IPFSEntry}
	 * @param storePath Enter a path that store the user configuration
	 */
	public IPFSParameter(IPFSEntry entry, String storePath)  {
		this.entry = entry;
		this.keyStorePath = storePath;
	}

	/**
	 * Get current drive type
	 * @return Current drive type
	 */
	@Override
	public DriveType getDriveType() {
		return DriveType.hiveIpfs;
	}

	/**
	 * Get current user auth entry
	 * @return Current auth entry
	 */
	@Override
	public IPFSEntry getAuthEntry() {
		return this.entry;
	}

	/**
	 * Get current user store data path
	 * @return Current store path
	 */
	@Override
	public String getKeyStorePath() {
		return keyStorePath;
	}
}
