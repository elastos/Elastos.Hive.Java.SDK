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

package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.DriveType;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Parameter;

/**
 * Hive sdk OneDrive parameter<br>
 * If you want to create an onedrive client, you need to create an onedrive parameter first
 */
public class OneDriveParameter implements Parameter<OAuthEntry> {
	private final OAuthEntry authEntry;
	private final String keyStorePath;

	/**
	 * OneDriveParameter constructor
	 * @param data User OAuthEntry {@link OAuthEntry}
	 * @param storePath Enter a path that store the user configuration
	 */
	public OneDriveParameter(OAuthEntry data, String storePath) {
		this.authEntry = data;
		this.keyStorePath = storePath;
	}

	/**
	 * Get current drive type
	 * @return Current drive type
	 */
	@Override
	public DriveType getDriveType() {
		return DriveType.oneDrive;
	}

	/**
	 * Get current user auth entry
	 * @return Current auth entry
	 */
	@Override
	public OAuthEntry getAuthEntry() {
		return authEntry;
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
