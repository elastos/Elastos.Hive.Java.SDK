package org.elastos.hive.vendors.onedrive.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Package: org.elastos.hive.vendors.onedrive.Model
 * ClassName: DirChildrenResponse
 * Created by ranwang on 2019/6/24.
 */
/*
{
	"@odata.count": 2,
	"@odata.context": "https://graph.microsoft.com/v1.0/$metadata#users('15011297029%40163.com')/drive/root/children",
	"value": [{
		"lastModifiedDateTime": "2019-06-24T08:31:04.533Z",
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
		"createdDateTime": "2019-06-24T08:31:04.533Z",
		"parentReference": {
			"path": "/drive/root:/autoTestDir",
			"driveId": "663768767e19dfc2",
			"driveType": "personal",
			"name": "autoTestDir",
			"id": "663768767E19DFC2!249"
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
		"webUrl": "https://1drv.ms/f/s!AMLfGX52aDdmgXw",
		"name": "createDir",
		"cTag": "adDo2NjM3Njg3NjdFMTlERkMyITI1Mi42MzY5Njk2MTg2NDUzMzAwMDA",
		"eTag": "aNjYzNzY4NzY3RTE5REZDMiEyNTIuMA",
		"id": "663768767E19DFC2!252",
		"fileSystemInfo": {
			"lastModifiedDateTime": "2019-06-24T08:31:04.533Z",
			"createdDateTime": "2019-06-24T08:31:04.533Z"
		}
	}, {
		"lastModifiedDateTime": "2019-06-24T09:47:22.807Z",
		"@microsoft.graph.downloadUrl": "https://public.bn.files.1drv.com/y4m2oyzTXu1QidxhM1X5nQ7VRi_uRpH-m7TvueZIWokS4L_H-CUFEstmCzeMw2MMd_Jxl4aJnTisjxmTvWHjC_68Gxc6Kq8LkuHTuSbfkQAvtBCym7gcLSUU3WMpLOs1Ir5uFYf1qwV41bSWZgLVAktdYODwxqdMpSB9bWGQ0eFL2zaNLkl3Pi_kMXkHVj9BlOtnkiLL5qouA06fStVx0e8Mf7vXuoTW9AsXoyOUzKVwxk",
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
		"createdDateTime": "2019-06-24T09:47:22.807Z",
		"parentReference": {
			"path": "/drive/root:/autoTestDir",
			"driveId": "663768767e19dfc2",
			"driveType": "personal",
			"name": "autoTestDir",
			"id": "663768767E19DFC2!249"
		},
		"file": {
			"hashes": {
				"quickXorHash": "AAAAAAAAAAAAAAAAAAAAAAAAAAA=",
				"sha1Hash": "DA39A3EE5E6B4B0D3255BFEF95601890AFD80709"
			},
			"mimeType": "application/octet-stream"
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
		"webUrl": "https://1drv.ms/u/s!AMLfGX52aDdmgX4",
		"name": "createFile",
		"cTag": "aYzo2NjM3Njg3NjdFMTlERkMyITI1NC4yNTc",
		"eTag": "aNjYzNzY4NzY3RTE5REZDMiEyNTQuMA",
		"id": "663768767E19DFC2!254",
		"fileSystemInfo": {
			"lastModifiedDateTime": "2019-06-24T09:47:22.806Z",
			"createdDateTime": "2019-06-24T09:47:22.806Z"
		}
	}]
}
 */
public class DirChildrenResponse {
    private List<ValueBean> value;

    public List<ValueBean> getValue() {
        return value;
    }

    public void setValue(List<ValueBean> value) {
        this.value = value;
    }

    public static class ValueBean {
        /**
         * lastModifiedDateTime : 2019-06-24T08:31:04.533Z
         * lastModifiedBy : {"application":{"displayName":"HiveTest","id":"4827420a"},"user":{"displayName":"","id":"663768767e19dfc2"}}
         * createdDateTime : 2019-06-24T08:31:04.533Z
         * parentReference : {"path":"/drive/root:/autoTestDir","driveId":"663768767e19dfc2","driveType":"personal","name":"autoTestDir","id":"663768767E19DFC2!249"}
         * folder : {"view":{"sortOrder":"ascending","viewType":"thumbnails","sortBy":"name"},"childCount":0}
         * size : 0
         * createdBy : {"application":{"displayName":"HiveTest","id":"4827420a"},"user":{"displayName":"","id":"663768767e19dfc2"}}
         * webUrl : https://1drv.ms/f/s!AMLfGX52aDdmgXw
         * name : createDir
         * cTag : adDo2NjM3Njg3NjdFMTlERkMyITI1Mi42MzY5Njk2MTg2NDUzMzAwMDA
         * eTag : aNjYzNzY4NzY3RTE5REZDMiEyNTIuMA
         * id : 663768767E19DFC2!252
         * fileSystemInfo : {"lastModifiedDateTime":"2019-06-24T08:31:04.533Z","createdDateTime":"2019-06-24T08:31:04.533Z"}
         * @microsoft.graph.downloadUrl : https://public.bn.files.1drv.com/y4m2oyzTXu1QidxhM1X5nQ7VRi_uRpH-m7TvueZIWokS4L_H-CUFEstmCzeMw2MMd_Jxl4aJnTisjxmTvWHjC_68Gxc6Kq8LkuHTuSbfkQAvtBCym7gcLSUU3WMpLOs1Ir5uFYf1qwV41bSWZgLVAktdYODwxqdMpSB9bWGQ0eFL2zaNLkl3Pi_kMXkHVj9BlOtnkiLL5qouA06fStVx0e8Mf7vXuoTW9AsXoyOUzKVwxk
         * file : {"hashes":{"quickXorHash":"AAAAAAAAAAAAAAAAAAAAAAAAAAA=","sha1Hash":"DA39A3EE5E6B4B0D3255BFEF95601890AFD80709"},"mimeType":"application/octet-stream"}
         */

        private String lastModifiedDateTime;
        private LastModifiedByBean lastModifiedBy;
        private String createdDateTime;
        private ParentReferenceBean parentReference;
        private FolderBean folder;
        private int size;
        private CreatedByBean createdBy;
        private String webUrl;
        private String name;
        private String cTag;
        private String eTag;
        private String id;
        private FileSystemInfoBean fileSystemInfo;
        @SerializedName("@microsoft.graph.downloadUrl")
        private String _$MicrosoftGraphDownloadUrl314; // FIXME check this code
        private FileBean file;

        public String getLastModifiedDateTime() {
            return lastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.lastModifiedDateTime = lastModifiedDateTime;
        }

        public LastModifiedByBean getLastModifiedBy() {
            return lastModifiedBy;
        }

        public void setLastModifiedBy(LastModifiedByBean lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
        }

        public String getCreatedDateTime() {
            return createdDateTime;
        }

        public void setCreatedDateTime(String createdDateTime) {
            this.createdDateTime = createdDateTime;
        }

        public ParentReferenceBean getParentReference() {
            return parentReference;
        }

        public void setParentReference(ParentReferenceBean parentReference) {
            this.parentReference = parentReference;
        }

        public FolderBean getFolder() {
            return folder;
        }

        public void setFolder(FolderBean folder) {
            this.folder = folder;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public CreatedByBean getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(CreatedByBean createdBy) {
            this.createdBy = createdBy;
        }

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCTag() {
            return cTag;
        }

        public void setCTag(String cTag) {
            this.cTag = cTag;
        }

        public String getETag() {
            return eTag;
        }

        public void setETag(String eTag) {
            this.eTag = eTag;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public FileSystemInfoBean getFileSystemInfo() {
            return fileSystemInfo;
        }

        public void setFileSystemInfo(FileSystemInfoBean fileSystemInfo) {
            this.fileSystemInfo = fileSystemInfo;
        }

        public String get_$MicrosoftGraphDownloadUrl314() {
            return _$MicrosoftGraphDownloadUrl314;
        }

        public void set_$MicrosoftGraphDownloadUrl314(String _$MicrosoftGraphDownloadUrl314) {
            this._$MicrosoftGraphDownloadUrl314 = _$MicrosoftGraphDownloadUrl314;
        }

        public FileBean getFile() {
            return file;
        }

        public void setFile(FileBean file) {
            this.file = file;
        }

        public static class LastModifiedByBean {
            /**
             * application : {"displayName":"HiveTest","id":"4827420a"}
             * user : {"displayName":"","id":"663768767e19dfc2"}
             */

            private ApplicationBean application;
            private UserBean user;

            public ApplicationBean getApplication() {
                return application;
            }

            public void setApplication(ApplicationBean application) {
                this.application = application;
            }

            public UserBean getUser() {
                return user;
            }

            public void setUser(UserBean user) {
                this.user = user;
            }

            public static class ApplicationBean {
                /**
                 * displayName : HiveTest
                 * id : 4827420a
                 */

                private String displayName;
                private String id;

                public String getDisplayName() {
                    return displayName;
                }

                public void setDisplayName(String displayName) {
                    this.displayName = displayName;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }
            }

            public static class UserBean {
                /**
                 * displayName :
                 * id : 663768767e19dfc2
                 */

                private String displayName;
                private String id;

                public String getDisplayName() {
                    return displayName;
                }

                public void setDisplayName(String displayName) {
                    this.displayName = displayName;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }
            }
        }

        public static class ParentReferenceBean {
            /**
             * path : /drive/root:/autoTestDir
             * driveId : 663768767e19dfc2
             * driveType : personal
             * name : autoTestDir
             * id : 663768767E19DFC2!249
             */

            private String path;
            private String driveId;
            private String driveType;
            private String name;
            private String id;

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getDriveId() {
                return driveId;
            }

            public void setDriveId(String driveId) {
                this.driveId = driveId;
            }

            public String getDriveType() {
                return driveType;
            }

            public void setDriveType(String driveType) {
                this.driveType = driveType;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }

        public static class FolderBean {
            /**
             * view : {"sortOrder":"ascending","viewType":"thumbnails","sortBy":"name"}
             * childCount : 0
             */

            private ViewBean view;
            private int childCount;

            public ViewBean getView() {
                return view;
            }

            public void setView(ViewBean view) {
                this.view = view;
            }

            public int getChildCount() {
                return childCount;
            }

            public void setChildCount(int childCount) {
                this.childCount = childCount;
            }

            public static class ViewBean {
                /**
                 * sortOrder : ascending
                 * viewType : thumbnails
                 * sortBy : name
                 */

                private String sortOrder;
                private String viewType;
                private String sortBy;

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

        public static class CreatedByBean {
            /**
             * application : {"displayName":"HiveTest","id":"4827420a"}
             * user : {"displayName":"","id":"663768767e19dfc2"}
             */

            private ApplicationBeanX application;
            private UserBeanX user;

            public ApplicationBeanX getApplication() {
                return application;
            }

            public void setApplication(ApplicationBeanX application) {
                this.application = application;
            }

            public UserBeanX getUser() {
                return user;
            }

            public void setUser(UserBeanX user) {
                this.user = user;
            }

            public static class ApplicationBeanX {
                /**
                 * displayName : HiveTest
                 * id : 4827420a
                 */

                private String displayName;
                private String id;

                public String getDisplayName() {
                    return displayName;
                }

                public void setDisplayName(String displayName) {
                    this.displayName = displayName;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }
            }

            public static class UserBeanX {
                /**
                 * displayName :
                 * id : 663768767e19dfc2
                 */

                private String displayName;
                private String id;

                public String getDisplayName() {
                    return displayName;
                }

                public void setDisplayName(String displayName) {
                    this.displayName = displayName;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }
            }
        }

        public static class FileSystemInfoBean {
            /**
             * lastModifiedDateTime : 2019-06-24T08:31:04.533Z
             * createdDateTime : 2019-06-24T08:31:04.533Z
             */

            private String lastModifiedDateTime;
            private String createdDateTime;

            public String getLastModifiedDateTime() {
                return lastModifiedDateTime;
            }

            public void setLastModifiedDateTime(String lastModifiedDateTime) {
                this.lastModifiedDateTime = lastModifiedDateTime;
            }

            public String getCreatedDateTime() {
                return createdDateTime;
            }

            public void setCreatedDateTime(String createdDateTime) {
                this.createdDateTime = createdDateTime;
            }
        }

        public static class FileBean {
            /**
             * hashes : {"quickXorHash":"AAAAAAAAAAAAAAAAAAAAAAAAAAA=","sha1Hash":"DA39A3EE5E6B4B0D3255BFEF95601890AFD80709"}
             * mimeType : application/octet-stream
             */

            private HashesBean hashes;
            private String mimeType;

            public HashesBean getHashes() {
                return hashes;
            }

            public void setHashes(HashesBean hashes) {
                this.hashes = hashes;
            }

            public String getMimeType() {
                return mimeType;
            }

            public void setMimeType(String mimeType) {
                this.mimeType = mimeType;
            }

            public static class HashesBean {
                /**
                 * quickXorHash : AAAAAAAAAAAAAAAAAAAAAAAAAAA=
                 * sha1Hash : DA39A3EE5E6B4B0D3255BFEF95601890AFD80709
                 */

                private String quickXorHash;
                private String sha1Hash;

                public String getQuickXorHash() {
                    return quickXorHash;
                }

                public void setQuickXorHash(String quickXorHash) {
                    this.quickXorHash = quickXorHash;
                }

                public String getSha1Hash() {
                    return sha1Hash;
                }

                public void setSha1Hash(String sha1Hash) {
                    this.sha1Hash = sha1Hash;
                }
            }
        }
    }
}
