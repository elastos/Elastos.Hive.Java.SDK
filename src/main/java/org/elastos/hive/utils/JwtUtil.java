package org.elastos.hive.utils;

import org.elastos.did.jwt.*;
import org.elastos.hive.exception.HiveException;

public class JwtUtil {

    public static JwsHeader getHeader(String jwt) throws HiveException {
        try {
            JwtParser jwtParser = new JwtParserBuilder().build();
            Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);
            return jws.getHeader();
        } catch (Exception e) {
            throw new HiveException("Cannot parse jwt token for header.");
        }
    }

    public static Claims getBody(String jwt) throws HiveException {
        try {
            JwtParser jwtParser = new JwtParserBuilder().build();
            Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);
            return jws.getBody();
        } catch (Exception e) {
            throw new HiveException("Cannot parse jwt token for body.");
        }
    }
}
