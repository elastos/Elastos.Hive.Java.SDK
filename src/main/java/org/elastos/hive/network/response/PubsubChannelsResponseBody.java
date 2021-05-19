package org.elastos.hive.network.response;

import org.elastos.hive.connection.HiveResponseBody;

import java.util.List;

public class PubsubChannelsResponseBody extends HiveResponseBody {
    private List<String> channels;

    public List<String> getChannels() {
        return this.channels;
    }
}
