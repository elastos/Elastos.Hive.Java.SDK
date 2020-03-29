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

import org.elastos.hive.result.Void;

import java.util.concurrent.CompletableFuture;

/**
 * Provides author-related functionality
 */
public interface AuthHelper {
	/**
	 * get AuthToken
	 * @return Current authtoken
	 */
	AuthToken getToken();

	/**
	 * Login async with authenticator instance
	 * @param authenticator authenticator instance
	 * @return Returns 'Void' if the login succees
	 */
	CompletableFuture<Void> loginAsync(Authenticator authenticator);

	/**
	 * Login async with authenticator instance
	 * @param authenticator authenticator instance
	 * @param callback Callback loginAsync result
	 * @return Returns 'Void' if the login succees
	 */
	CompletableFuture<Void> loginAsync(Authenticator authenticator, Callback<Void> callback);

	/**
	 * Logout account
	 * @return Returns `Void` if the logout succees
	 */
	CompletableFuture<Void>	logoutAsync();

	/**
	 * Logout account
	 * @param callback Callback logoutAsync result
	 * @return Returns `Void` if the logout succees
	 */
	CompletableFuture<Void> logoutAsync(Callback<Void> callback);

	/**
	 * Check access token isExpired
	 * @return Returns `Void` if expired
	 */
	CompletableFuture<Void> checkExpired();

	/**
	 * Check access token isExpired
	 * @param callback Callback checkExpired result
	 * @return Returns `Void` if expired
	 */
	CompletableFuture<Void> checkExpired(Callback<Void> callback);
}
