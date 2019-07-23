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
	"lastModifiedDateTime": "2019-06-21T07:34:15.74Z",
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
	"createdDateTime": "2019-06-21T07:34:15.43Z",
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
		"childCount": 1
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
	"webUrl": "https://1drv.ms/f/s!AMLfGX52aDdmag",
	"name": "root",
	"cTag": "adDo2NjM3Njg3NjdFMTlERkMyITEwNi42MzY5NjY5OTI1NTc0MDAwMDA",
	"eTag": "aNjYzNzY4NzY3RTE5REZDMiExMDYuMA",
	"id": "663768767E19DFC2!106",
	"fileSystemInfo": {
		"lastModifiedDateTime": "2019-06-21T07:34:15.43Z",
		"createdDateTime": "2019-06-21T07:34:15.43Z"
	}
}
 */
public class FileOrDirPropResponse {
    private String id ;
    private String name;
    private int size;
    private Folder folder;

    public FileOrDirPropResponse(String id) {
        this.id = id;
    }

    public FileOrDirPropResponse(String id, Folder folder) {
        this.id = id;
        this.folder = folder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public class Folder {
        private View view ;
        private int childCount ;

        public Folder(View view, int childCount) {
            this.view = view;
            this.childCount = childCount;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public int getChildCount() {
            return childCount;
        }

        public void setChildCount(int childCount) {
            this.childCount = childCount;
        }

        public class View {
            String sortOrder ;
            String viewType ;
            String sortBy ;

            public View(String sortOrder, String viewType, String sortBy) {
                this.sortOrder = sortOrder;
                this.viewType = viewType;
                this.sortBy = sortBy;
            }

            public String getSortOrder() {
                return sortOrder;
            }

            public void setSortOrder(String sortOrder) {
                this.sortOrder = sortOrder;
            }

            public String getViewType() {
                return viewType;
            }

            public void setViewType(String viewType) {
                this.viewType = viewType;
            }

            public String getSortBy() {
                return sortBy;
            }

            public void setSortBy(String sortBy) {
                this.sortBy = sortBy;
            }
        }

    }
}
