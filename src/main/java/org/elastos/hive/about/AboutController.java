package org.elastos.hive.about;

import java.io.IOException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;

import retrofit2.Response;

public class AboutController {
	private AboutAPI aboutAPI;

	public AboutController(ServiceEndpoint serviceEndpoint) {
		aboutAPI = serviceEndpoint.getConnectionManager()
					.createRetrofit(false)
					.create(AboutAPI.class);
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
		Response<CommitHash> response;
		try {
			response = aboutAPI.commitId().execute();
			if (response.isSuccessful())
				return response.body().getCommitId();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
