package org.elastos.hive;

import java.util.concurrent.Semaphore;

import fi.iki.elonen.NanoHTTPD;

public final class AuthServer extends NanoHTTPD{
	private Semaphore authLock;
	private String authCode = "";

	public AuthServer(Semaphore semaphore , String host , int port){
        this(host,port);
        authLock = semaphore;
		try {
			authLock.acquire();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public AuthServer(String host, int port) {
		super(host,port);
    }

	@Override
	public Response serve(IHTTPSession session){
		StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("</body></html>\n");

        String[] query = session.getQueryParameterString().split("=");

        switch (query[0]) {
            case "code":
                authCode = query[1];
                break;
            case "error":
            default:
                break;
        }

        if(authLock!=null){
            authLock.release();
        }
        return newFixedLengthResponse(builder.toString());
    }

    public String getAuthCode() {
        return authCode;
    }
}
