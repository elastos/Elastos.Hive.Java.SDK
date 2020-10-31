package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

/*
 * This is the interface to make authorization from users, and it would be
 * provided by application.
 *
 * It will be called when Hive SDK receives the authentication challege
 * from backend service, and Hive SDK will use this interface to acquire
 * the credential from DID application and became agent to user to interact
 * with backend service.
 */
public interface AuthenticationHandler {
	/*
	 * Acquire the did authentication credential from DID application.
	 *
	 * The application would implement this interface method, and would
	 *   verify this jwttoken valid or not
	 *   extract issuer and nonce
	 *
	 * Then applciation would generate application instance presentation including
	 *   nonce=nonce,
	 *   real=iss
	 *   application id crednetial embeded presentail
	 * as JWT and return to this Hive SDK
	 *
	 * Hive SDK to forward the jwttoken string to backend service, where backend service
	 * would verify jwt token (using local app instance did public key provided before)
	 *
	 * And if checked in success, then  will generate access token and response back
	 * to Hive SDK
	 * Hive SDK would use this access token to access/update resources on backend
	 * service in the next.
	 *
	 * @param jwtToken the token as authentication challenge request in jwt string.
	 * @return The completableFuture object containing the authentication credential.
	 * @throws HiveException
	 */
	CompletableFuture<String> authenticationChallenge(String jwtToken);
}

