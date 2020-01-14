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

package org.elastos.hive.utils;

public class UrlUtil {
    public static String[] decodeHostAndPort(String requestUrl, String defaultHost, String defaultPort) {
        String[] hostAndPort = new String[2];

        if (requestUrl.contains("//")) {
            String[] urlInfo = requestUrl.split("//");
            requestUrl = urlInfo[1];
        }

        hostAndPort[0] = defaultHost;
        hostAndPort[1] = defaultPort;

        String[] hostInfo = null;
        if (requestUrl.contains(":")) {
            hostInfo = requestUrl.split(":");
        }

        if (null != hostInfo && hostInfo.length == 2) {
            try {
                hostAndPort[0] = hostInfo[0];
                hostAndPort[1] = hostInfo[1];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return hostAndPort;
    }

    public static String checkPort(String requestUrl, String defaultPort) {
        if (requestUrl.contains(":")) {
            return requestUrl;
        }

        return requestUrl + ":" + defaultPort;

    }

    public static boolean checkAddress(String ipStr) {
        return ipStr.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
    }

    public static boolean checkPort(String portStr) {
        return portStr.matches("^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)");
    }
}
