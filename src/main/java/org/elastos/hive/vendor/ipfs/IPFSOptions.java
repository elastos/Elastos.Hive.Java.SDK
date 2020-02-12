package org.elastos.hive.vendor.ipfs;

import java.util.ArrayList;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;

public class IPFSOptions extends Client.Options {
    private ArrayList<RpcNode> rpcNodes;

    private IPFSOptions() {
        rpcNodes = new ArrayList<>();
    }

    private void addRpcNode(RpcNode node) {
        rpcNodes.add(node);
    }

    public ArrayList<RpcNode> getRpcNodes() {
        return rpcNodes;
    }

    @Override
    protected boolean checkValid(boolean all) {
        return (rpcNodes.size() > 0 && super.checkValid(all));
    }

    boolean checkValid() {
        return checkValid(false);
    }

    public static class RpcNode {
        private String ipv4;
        private String ipv6;
        private int port;

        public RpcNode(String ipv4, int port) {
            this.ipv4 = ipv4;
            this.port = port;
        }

        public RpcNode(String ipv4, String ipv6, int port) {
            this(ipv4, port);
            this.ipv6 = ipv6;
        }

        String getIpv4() {
            return ipv4;
        }

        String getIpv6() {
            return ipv6;
        }

        int getPort() {
            return port;
        }
    }

    public static class Builder {
        IPFSOptions options;

        public Builder() {
            options = new IPFSOptions();
        }

        public Builder setStorePath(String path) {
            options.setStorePath(path);
            return this;
        }

        public Builder addRpcNode(RpcNode node) {
            options.addRpcNode(node);
            return this;
        }

        public Client.Options build() throws HiveException {
            if (options == null) {
                throw new HiveException("Builder deprecated");
            }
            if (!options.checkValid()) {
                throw new HiveException("Missing options");
            }

            Client.Options opts = options;
            this.options = null;
            return opts;
        }
    }

    @Override
    protected Client buildClient() {
        return new IPFSClient(this);
    }
}
