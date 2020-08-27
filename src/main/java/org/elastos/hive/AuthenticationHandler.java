package org.elastos.hive;

public interface AuthenticationHandler {
    /*
     * auth challenge: JWT (iss, nonce)
     * hive sdk:
     *   verify jwt
     *   extract iss and nonce
     * consumer dapp:
     *   generate app instance presentation including nonce=nonce, realm=iss, app id credential
     *   embed presentation as JWT and return to the hive auth handler
     * server side:
     *   verify jwt (using local app instance did public key provided before)
     *   generate access token
     */
    public String authenticationChallenge(String jwtToken);
}

