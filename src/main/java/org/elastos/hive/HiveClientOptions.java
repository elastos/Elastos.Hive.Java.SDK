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

package org.elastos.hive;

public class HiveClientOptions {
    public static final String DEFAULT_STORE_PATH = System.getProperty("user.dir");
    private Persistent persistent ;

    public HiveClientOptions(Persistent persistent) {
        setPersistent(persistent);
    }

    public HiveClientOptions(String storePath) {
        setPersistent(storePath);
    }

    public HiveClientOptions() {
        setPersistent(HiveClientOptions.DEFAULT_STORE_PATH);
    }

    Persistent getPersistent() {
        return persistent;
    }

    private void setPersistent(Persistent persistent) {
        this.persistent = persistent;
    }

    private void setPersistent(String storePath) {
        this.persistent = new AuthInfoStoreImpl(storePath);
    }
}
