package org.elastos.hive;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class IpfsRunner {
    private String ipfsGatewayUrl;

    public IpfsRunner() {
        this(null);
    }

    public IpfsRunner(String ipfsGatewayUrl) {
        this.ipfsGatewayUrl = ipfsGatewayUrl == null ? "https://ipfs.trinity-tech.io" : ipfsGatewayUrl;
    }

    public CompletableFuture<InputStream> getFileStream(String cid) {
        return CompletableFuture.supplyAsync(() -> {
            if (cid == null || cid.isEmpty())
                throw new IllegalArgumentException("Empty cid parameter");

            try {
                return new BufferedInputStream(this.getFileInputStream(this.ipfsGatewayUrl + "/ipfs/" + cid));
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    private InputStream getFileInputStream(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        return connection.getInputStream();
    }

    public CompletableFuture<Reader> getFileReader(String cid) {
        return CompletableFuture.supplyAsync(() -> {
            if (cid == null || cid.isEmpty())
                throw new IllegalArgumentException("Empty cid parameter");

            try {
                return new InputStreamReader(this.getFileInputStream(this.ipfsGatewayUrl + "/ipfs/" + cid));
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

}
