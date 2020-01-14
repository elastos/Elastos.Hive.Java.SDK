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

package org.elastos.hive.vendor.onedrive.network.model;

import java.util.List;

/*
{
	"@odata.context":"https://graph.microsoft.com/v1.0/$metadata#microsoft.graph.uploadSession",
	"expirationDateTime":"2019-09-12T08:54:35.838Z",
	"nextExpectedRanges":["0-"],
	"uploadUrl":"https://api.onedrive.com/rup/fd10af65189a8cca/eyJSZXNvdXJjZUlEIjoiRkQxMEFGNjUxODlBOENDQSExMDEiLCJSZWxhdGlvbnNoaXBOYW1lIjoib25lZHJpdmVfdGVzdEZpbGVXcml0ZS50eHQifQ/4mp_PrdNMEB_lqxlwgA6S_bsb379E0OalM39kCqovBFGLfOIvwbjnmxZVCmVZ7lnQZk7yRYgocfB0PuAgNctazMte5RatF30tyVszkt4m197E/eyJuYW1lIjoib25lZHJpdmVfdGVzdEZpbGVXcml0ZS50eHQiLCJAbmFtZS5jb25mbGljdEJlaGF2aW9yIjoicmVwbGFjZSJ9/4wSbBE3w9GmcPT_S0K7MM58ssOWFc2p12BgAPiMaK4XQ52r_QSj9b4kI0iMKP6OEOaR1dwpYFoDS_C0wLBRW_d7_Lr1Vwv5YepYoX8dMKZdxOVtGV3HIfL1JR1ELyUf7UeKfbFyHJ8VzVNPR1F5OFfObKaksjDtVV_wuGOggzO9dlEcfUVdCszeRld6oCk3ALkqZG_9lTea2XzmBnanb_6MEuCDHUZBR1olkRNHt8UnLDFarbJaH8y0YdazPf6CcQQpo3dsEWnZBwrTHwHUUOM5jdzpP2kPKcEBUOI8Jyvydv1ULjCMwLD1AqkGWqkHvVx6L0vvrYOvkAMF7xTIYpVXrRqwqUFQMyDrmAAa5G6w0Vv1ZIB4gqhStQ6X41d5RzkX799cfWUA-xzw4nzXajoGZq9A0_N0EE3CsH5nnQUAVjYj7N5Rnyfd2OOi4x-WFD-T-RbW6oX3e9cUJpiZao741YKQ1LmmeulPBi-X0pK1DPmxvPbQFGWiRLCwL_hdkUWd2-PI8zOBDWJJZSyDhUVOkKvdujb1W_XQBvU5mq49wvOa4QwZwiiYHeSFHsG5W1C"
}
*/

public class UploadSessionResponse {
    private String context;
    private String expirationDateTime;
    private List<String> nextExpectedRanges;
    private String uploadUrl;

    public UploadSessionResponse(String context, String expirationDateTime, List<String> nextExpectedRanges, String uploadUrl) {
        this.context = context;
        this.expirationDateTime = expirationDateTime;
        this.nextExpectedRanges = nextExpectedRanges;
        this.uploadUrl = uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getUploadUrl() {
        return this.uploadUrl;
    }

    @Override
    public String toString() {
        return "UploadSessionResponse {" +
                "context='" + context + '\'' +
                ", expirationDateTime='" + expirationDateTime + '\'' +
                ", nextExpectedRanges=" + nextExpectedRanges +
                ", uploadUrl=" + uploadUrl +
                '}';
    }
}
