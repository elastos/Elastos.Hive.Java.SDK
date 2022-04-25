package org.elastos.hive.utils;

import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.ExpiredJwtException;
import org.elastos.did.jwt.Jws;
import org.elastos.did.jwt.JwsHeader;
import org.elastos.did.jwt.JwsSignatureException;
import org.elastos.did.jwt.JwtParser;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.did.jwt.MalformedJwtException;
import org.elastos.did.jwt.UnsupportedJwtException;

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
            JwtParser jwtParser = new JwtParserBuilder().setAllowedClockSkewSeconds(300).build();
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
