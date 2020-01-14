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

package org.elastos.hive.vendor.ipfs.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class ListFileResponse {

    /**
     * Arguments : {"QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn":"QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn"}
     * Objects : {"QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn":{"Hash":"QmUNLLsPACCz1vLxQVkXqqLX5R1X345qqfHbsf67hvA3Nn","Size":0,"Type":"Directory","Links":[]}}
     */
    @SerializedName("Arguments")
    private HashMap<String, String> Arguments;
    @SerializedName("Objects")
    private HashMap<String, ObjectsBean.Bean> Objects;

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
