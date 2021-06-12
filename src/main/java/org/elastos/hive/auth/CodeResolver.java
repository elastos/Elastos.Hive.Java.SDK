package org.elastos.hive.auth;

import org.elastos.hive.exception.NodeRPCException;

public interface CodeResolver {
	String resolve() throws NodeRPCException;
	void invalidate();
}
