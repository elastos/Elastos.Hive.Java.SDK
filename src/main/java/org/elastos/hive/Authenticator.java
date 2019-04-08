package org.elastos.hive;

public abstract class Authenticator {
	public abstract AuthResult requestAuthentication(String requestUrl);
}
