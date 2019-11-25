package org.elastos.hive.vendors.ipfs.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ListFileResponse {

    /**
     * Arguments : {"QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn":"QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn"}
     * Objects : {"QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn":{"Hash":"QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn","Size":0,"Type":"Directory","Links":[]}}
     */
    @SerializedName("Arguments")
    private HashMap<String , String> Arguments ;
    @SerializedName("Objects")
    private HashMap<String , ObjectsBean.Bean> Objects ;

    public HashMap<String, ObjectsBean.Bean> getObjects() {
        return Objects;
    }

    public static class ObjectsBean {
        public static class Bean {
            private String Hash;
            private int Size;
            private String Type;
            private List<?> Links;

            public String getHash() {
                return Hash;
            }

            public void setHash(String Hash) {
                this.Hash = Hash;
            }

            public int getSize() {
                return Size;
            }

            public void setSize(int Size) {
                this.Size = Size;
            }

            public String getType() {
                return Type;
            }

            public void setType(String Type) {
                this.Type = Type;
            }

            public List<?> getLinks() {
                return Links;
            }

            public void setLinks(List<?> Links) {
                this.Links = Links;
            }
        }
    }
}
