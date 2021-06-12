package org.elastos.hive.auth;

import org.elastos.hive.connection.NodeRPCException;

public interface CodeResolver {
	String resolve() throws NodeRPCException;
	void invalidate();
}
