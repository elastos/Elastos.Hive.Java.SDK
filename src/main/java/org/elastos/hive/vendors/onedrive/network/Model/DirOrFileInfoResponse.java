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

package org.elastos.hive.vendors.onedrive.network.model;

/*
{
	"lastModifiedDateTime": "2019-06-24T02:46:03.52Z",
	"lastModifiedBy": {
		"application": {
			"displayName": "HiveTest",
			"id": "4827420a"
		},
		"user": {
			"displayName": "",
			"id": "663768767e19dfc2"
		}
	},
	"createdDateTime": "2019-06-24T02:46:03.52Z",
	"@odata.context": "https://graph.microsoft.com/v1.0/$metadata#users('15011297029%40163.com')/drive/root/$entity",
	"parentReference": {
		"path": "/drive/root:",
		"driveId": "663768767e19dfc2",
		"driveType": "personal",
		"id": "663768767E19DFC2!101"
	},
	"folder": {
		"view": {
			"sortOrder": "ascending",
			"viewType": "thumbnails",
			"sortBy": "name"
		},
		"childCount": 0
	},
	"size": 0,
	"createdBy": {
		"application": {
			"displayName": "HiveTest",
			"id": "4827420a"
		},
		"user": {
			"displayName": "",
			"id": "663768767e19dfc2"
		}
	},
	"webUrl": "https://1drv.ms/f/s!AMLfGX52aDdmgSg",
	"name": "testDirectory1561344364328",
	"cTag": "adDo2NjM3Njg3NjdFMTlERkMyITE2OC42MzY5Njk0MTE2MzUyMDAwMDA",
	"eTag": "aNjYzNzY4NzY3RTE5REZDMiExNjguMA",
	"id": "663768767E19DFC2!168",
	"fileSystemInfo": {
		"lastModifiedDateTime": "2019-06-24T02:46:03.52Z",
		"createdDateTime": "2019-06-24T02:46:03.52Z"
	}
}
 */
public class DirOrFileInfoResponse {
    private String id;

    public DirOrFileInfoResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
