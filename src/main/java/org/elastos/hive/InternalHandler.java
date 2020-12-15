package org.elastos.hive;

public interface InternalHandler {

	String authenticate(HiveContext context, String jwtToken);
}