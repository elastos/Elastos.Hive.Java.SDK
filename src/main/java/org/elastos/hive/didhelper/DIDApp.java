package org.elastos.hive.didhelper;

import org.elastos.did.Issuer;
import org.elastos.did.VerifiableCredential;
import org.elastos.did.exception.DIDException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

class DIDApp  extends Entity {
	private Issuer issuer;

	public DIDApp(String name, String mnemonic, String phrasepass, String storepass) throws DIDException {
		super(name, mnemonic, phrasepass, storepass);
		issuer = new Issuer(getDocument());
	}

	public VerifiableCredential issueDiplomaFor(DApp dapp) throws DIDException {
		Map<String, Object> subject = new HashMap<String, Object>();
		subject.put("appDid", dapp.appId);

		Calendar exp = Calendar.getInstance();
		exp.add(Calendar.YEAR, 5);

		VerifiableCredential.Builder cb = issuer.issueFor(dapp.getDid());
		VerifiableCredential vc = cb.id("didapp")
				.type("AppIdCredential")
				.properties(subject)
				.expirationDate(exp.getTime())
				.seal(getStorePassword());

		System.out.println("VerifiableCredential:");
		String vcStr = vc.toString();
		System.out.println(vcStr);

		return vc;
	}
}
