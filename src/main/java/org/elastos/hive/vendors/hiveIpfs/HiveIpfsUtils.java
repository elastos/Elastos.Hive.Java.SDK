package org.elastos.hive.vendors.hiveIpfs;

import org.elastos.hive.HiveException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class HiveIpfsUtils {
	static final String CONFIG      = "/ipfsConfig.properties";
	static final String BASEURL     = "http://52.83.159.189:9095/api/v0/";
	static final String PREFIX      = "/ipfs/";
	static final String CONTENTTYPE = "Content-Type";
	static final String TYPE_Json   = "application/json";
	static final String UID         = "uid";
	static final String HASH        = "hash";
	static final String PATH        = "path";

	static String getHomeHash() throws HiveException {
		String url = BASEURL + "name/resolve";
		try {
			HttpResponse<JsonNode> json = Unirest.get(url).header(CONTENTTYPE, TYPE_Json).asJson();
			return json.getBody().getObject().getString("Path"); 
		} catch (UnirestException e) {
			e.printStackTrace();
			throw new HiveException("Get home hash failed.");
		}
	}

	static String getNewUid() throws HiveException {
		String url = BASEURL + "uid/new";
		try {
			HttpResponse<JsonNode> json = Unirest.get(url).header(CONTENTTYPE, TYPE_Json).asJson();
			return json.getBody().getObject().getString("UID"); 
		} catch (UnirestException e) {
			e.printStackTrace();
			throw new HiveException("Get the new uid failed.");
		}
	}	
}
