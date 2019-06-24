package org.elastos.hive.vendors.onedrive.Model;

/**
 * Package: org.elastos.hive.vendors.onedrive.Model
 * ClassName: DirInfoResponse
 * Created by ranwang on 2019/6/24.
 */
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
public class DirInfoResponse {
    private String id;

    public DirInfoResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
