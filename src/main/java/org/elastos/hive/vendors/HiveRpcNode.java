package org.elastos.hive.vendors;

public class HiveRpcNode{
    String ipv4 ;
    String ipv6 ;
    int port ;

    public HiveRpcNode(String ipv4, int port) {
        this.ipv4 = ipv4;
        this.port = port;
    }

    public HiveRpcNode(String ipv4, String ipv6, int port) {
        this.ipv4 = ipv4;
        this.ipv6 = ipv6;
        this.port = port;
    }

    public String getIpv4() {
        return ipv4;
    }

    public String getIpv6() {
        return ipv6;
    }

    public int getPort() {
        return port;
    }
}
