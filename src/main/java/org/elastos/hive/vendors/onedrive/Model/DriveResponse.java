package org.elastos.hive.vendors.onedrive.Model;

/*
{
	"owner": {
		"user": {
			"displayName": "",
			"id": "663768767e19dfc2"
		}
	},
	"driveType": "personal",
	"quota": {
		"total": 5368709120,
		"deleted": 0,
		"state": "normal",
		"used": 1159342,
		"remaining": 5367549778
	},
	"@odata.context": "https://graph.microsoft.com/v1.0/$metadata#drives/$entity",
	"id": "663768767e19dfc2"
}
 */
public class DriveResponse {
    private Owner owner ;
    private String driveType ;
    private Quota quota ;
    private String id ;

    public DriveResponse(Owner owner, String driveType, String id) {
        this.owner = owner;
        this.driveType = driveType;
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DriveResponse{" +
                "owner=" + owner +
                ", driveType='" + driveType + '\'' +
                ", quota=" + quota +
                ", id='" + id + '\'' +
                '}';
    }

    public class Owner{
        User user;

        public Owner(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "Owner{" +
                    "user=" + user +
                    '}';
        }
    }

    public class User{
        private String displayName ;
        private String id ;

        public User(String displayName, String id) {
            this.displayName = displayName;
            this.id = id;
        }

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

        @Override
        public String toString() {
            return "User{" +
                    "displayName='" + displayName + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    class Quota{
        private long total ;
        private long deleted ;
        private String state ;
        private long used ;
        private long remaining ;

        public Quota(long total, long deleted, String state, long used, long remaining) {
            this.total = total;
            this.deleted = deleted;
            this.state = state;
            this.used = used;
            this.remaining = remaining;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getDeleted() {
            return deleted;
        }

        public void setDeleted(long deleted) {
            this.deleted = deleted;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public long getUsed() {
            return used;
        }

        public void setUsed(long used) {
            this.used = used;
        }

        public long getRemaining() {
            return remaining;
        }

        public void setRemaining(long remaining) {
            this.remaining = remaining;
        }

        @Override
        public String toString() {
            return "Quota{" +
                    "total=" + total +
                    ", deleted=" + deleted +
                    ", state='" + state + '\'' +
                    ", used=" + used +
                    ", remaining=" + remaining +
                    '}';
        }
    }
}
