package org.elastos.hive.connection;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class SHA256 {
    public static String generate(String src) {
        return Hashing.sha256()
                .hashString(src, StandardCharsets.UTF_8)
                .toString();
    }
}
