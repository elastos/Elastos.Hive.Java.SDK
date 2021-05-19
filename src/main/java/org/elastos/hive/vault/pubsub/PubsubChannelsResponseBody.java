package org.elastos.hive.vault.pubsub;

import org.elastos.hive.connection.HiveResponseBody;

import java.util.List;

class PubsubChannelsResponseBody extends HiveResponseBody {
    private List<String> channels;

    public List<String> getChannels() {
        return this.channels;
    }
}
