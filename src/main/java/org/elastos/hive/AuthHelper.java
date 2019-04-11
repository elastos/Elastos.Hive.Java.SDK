package org.elastos.hive;

import org.elastos.hive.exceptions.HiveException;

public interface AuthHelper {

	public boolean login(Authenticator authenticator) throws HiveException;

	public void checkExpired() throws HiveException;

	public AuthInfo getAuthInfo();
}
