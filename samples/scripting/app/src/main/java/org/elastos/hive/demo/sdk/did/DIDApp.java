package org.elastos.hive.demo.sdk.did;

import org.elastos.did.DID;
import org.elastos.did.Issuer;
import org.elastos.did.VerifiableCredential;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DIDApp  extends DIDEntity {
	private Issuer issuer;

	public DIDApp(String name, String mnemonic, DummyAdapter adapter,  String phrasepass, String storepass, String storeDir) throws DIDException {
		super(name, mnemonic, adapter, phrasepass, storepass, storeDir);
		issuer = new Issuer(getDocument());
	}

	public VerifiableCredential issueDiplomaFor(DApp dapp) throws DIDException {
		Map<String, String> subject = new HashMap<String, String>();
		subject.put("appDid", dapp.appId);

		Calendar exp = Calendar.getInstance();
		exp.add(Calendar.YEAR, 5);

		Issuer.CredentialBuilder cb = issuer.issueFor(dapp.getDid());
		VerifiableCredential vc = cb.id("didapp")
				.type("AppIdCredential")
				.properties(subject)
				.expirationDate(exp.getTime())
				.seal(getStorePassword());

		System.out.println("VerifiableCredential:");
		String vcStr = vc.toString();
		System.out.println(vcStr);

		if(!vc.isValid()) {
			throw new IllegalStateException("Verifiable Credential is invalid");
		}

		return vc;
	}

	public VerifiableCredential issueBackupDiplomaFor(String sourceDID, String targetHost, String targetDID) throws DIDException {
		Map<String, String> subject = new HashMap<String, String>();
		subject.put("sourceDID", sourceDID);
		subject.put("targetHost", targetHost);
		subject.put("targetDID", targetDID);

		Calendar exp = Calendar.getInstance();
		exp.add(Calendar.YEAR, 5);

		Issuer.CredentialBuilder cb = issuer.issueFor(new DID(sourceDID));
		VerifiableCredential vc = cb.id("backupId")
				.type("BackupCredential")
				.properties(subject)
				.expirationDate(exp.getTime())
				.seal(getStorePassword());

		System.out.println("BackupCredential:");
		String vcStr = vc.toString();
		System.out.println(vcStr);

		if(!vc.isValid()) {
			throw new IllegalStateException("Verifiable Credential is invalid");
		}

		return vc;
	}
}
