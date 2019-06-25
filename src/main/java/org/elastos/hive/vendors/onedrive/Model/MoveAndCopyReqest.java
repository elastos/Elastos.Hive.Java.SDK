package org.elastos.hive.vendors.onedrive.Model;

/**
 * Package: org.elastos.hive.vendors.onedrive.Model
 * ClassName: MoveAndCopyReqest
 * Created by ranwang on 2019/6/24.
 */
public class MoveAndCopyReqest {
    private ParentReference parentReference ;
    private String name ;

    public MoveAndCopyReqest(String pathName, String name) {
        this.parentReference = new ParentReference("/drive/root:"+pathName);
        this.name = name;
    }

    public ParentReference getParentReference() {
        return parentReference;
    }

    public void setParentReference(ParentReference parentReference) {
        this.parentReference = parentReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private class ParentReference{
        private String path ;

        public ParentReference(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }


}
