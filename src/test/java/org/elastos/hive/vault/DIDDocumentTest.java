/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vault;

import org.elastos.did.DIDDocument;
import org.elastos.did.DIDURL;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.vendor.vault.DIDData;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


public class DIDDocumentTest {

	@Test
	public void testSignAndVerify() throws DIDException, IOException {
		DIDData DIDData = new DIDData();
		DIDData.setup(true);
		DIDData.initIdentity();

		DIDDocument doc = DIDData.loadDocument();
		assertNotNull(doc);
		assertTrue(doc.isValid());

		DIDURL pkid = new DIDURL(doc.getSubject(), "primary");

		String keyName = "key2";
		String nonce = "4607e6de-b5f0-11ea-a859-f45c898fba57";

		String sig = doc.sign(pkid, TestConfig.storePass, nonce.getBytes());
		assertTrue(sig!=null);


//		for (int i = 0; i < 10; i++) {
//			Arrays.fill(data, (byte) i);
//
//			String sig = doc.sign(pkid, TestConfig.storePass, data);
//			boolean result = doc.verify(pkid, sig, data);
//			assertTrue(result);
//
//			data[0] = 0xF;
//			result = doc.verify(pkid, sig, data);
//			assertFalse(result);
//
//			sig = doc.sign(TestConfig.storePass, data);
//			result = doc.verify(sig, data);
//			assertTrue(result);
//
//			data[0] = (byte) i;
//			result = doc.verify(sig, data);
//			assertFalse(result);
//		}
	}
}
