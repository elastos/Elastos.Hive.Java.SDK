package org.elastos.hive.about;

import java.io.IOException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;

public class AboutController {
	private AboutAPI aboutAPI;

	public AboutController(ServiceEndpoint serviceEndpoint) {
		aboutAPI = serviceEndpoint.getConnectionManager().createService(AboutAPI.class, false);
	}

	public NodeVersion getNodeVersion() throws HiveException {
		try {
			return aboutAPI.version().execute().body();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getCommitId() throws HiveException {
		try {
			return aboutAPI.commitId().execute().body().getCommitId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
