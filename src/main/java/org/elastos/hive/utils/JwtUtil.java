package org.elastos.hive.utils;

import org.elastos.did.jwt.*;

public class JwtUtil {

    public static JwsHeader getHeader(String jwt) {
        try {
            JwtParser jwtParser = new JwtParserBuilder().build();
            Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);
            return jws.getHeader();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            e.printStackTrace();
        } catch (JwsSignatureException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Claims getBody(String jwt) {
        try {
            JwtParser jwtParser = new JwtParserBuilder().build();
            Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);
            return jws.getBody();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            e.printStackTrace();
        } catch (JwsSignatureException e) {
            e.printStackTrace();
        }

        return null;
    }
}
