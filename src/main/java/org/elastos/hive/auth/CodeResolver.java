package org.elastos.hive.auth;

import org.elastos.hive.exception.HttpFailedException;

public interface CodeResolver {
	String resolve() throws HttpFailedException;
	void invalidate();
}
