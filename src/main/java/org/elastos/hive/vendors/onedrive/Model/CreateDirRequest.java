package org.elastos.hive.vendors.onedrive.Model;

import com.google.gson.annotations.SerializedName;

public class CreateDirRequest {
    private String name ;

    private Folder folder = new Folder();

    //conflictBehavior' value : fail, replace, or rename
    @SerializedName("@microsoft.graph.conflictBehavior")
    private String behavior = "fail";

    public CreateDirRequest(String name) {
        this.name = name;
    }

    public CreateDirRequest(String name , Folder folder) {
        this.name = name;
        this.folder = folder ;
    }

    public CreateDirRequest(String name, Folder folder, String behavior) {
        this.name = name;
        this.folder = folder;
        this.behavior = behavior;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    @Override
    public String toString() {
        return "CreateDirRequest{" +
                "name='" + name + '\'' +
                ", folder=" + folder +
                ", behavior='" + behavior + '\'' +
                '}';
    }

    class Folder {
    }
}
