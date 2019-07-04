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

package org.elastos.hive.vendors.connection.Model;

public class BaseServiceConfig {

    private final boolean useGsonConverter ;
    private final boolean ignoreReturnbody;
    private final HeaderConfig headerConfig ;

    private BaseServiceConfig(){
        this.useGsonConverter = true;
        this.ignoreReturnbody = false;
        this.headerConfig = null ;
    }

    private BaseServiceConfig(Builder builder){
        this.useGsonConverter = builder.useGsonConverter;
        this.ignoreReturnbody = builder.ignoreReturnbody;
        this.headerConfig = builder.headerConfig ;
    }

    public boolean isUseGsonConverter() {
        return useGsonConverter;
    }

    public boolean isIgnoreReturnbody() {
        return ignoreReturnbody;
    }

    public HeaderConfig getHeaderConfig() {
        return headerConfig;
    }

    public static final class Builder {
        boolean useGsonConverter ;
        boolean ignoreReturnbody;
        HeaderConfig headerConfig ;

        public Builder() {
            this.useGsonConverter = true ;
            this.ignoreReturnbody = false ;
            this.headerConfig = null ;
        }

        public Builder useGsonConverter(boolean flag){
            this.useGsonConverter = flag ;
            return this;
        }

        public Builder ignoreReturnBody(boolean flag){
            this.ignoreReturnbody = flag ;
            return this;
        }

        public Builder headerConfig(HeaderConfig headerConfig){
            this.headerConfig = headerConfig ;
            return this;
        }

        public BaseServiceConfig build() {
            return new BaseServiceConfig(this);
        }
    }
}
