package org.elastos.hive.vault.pubsub;

import org.elastos.hive.connection.HiveResponseBody;

import java.util.List;

class PopMessageResponseBody extends HiveResponseBody {
    private List<ChannelMessage> messages;

    public List<ChannelMessage> getMessages() {
        return this.messages;
    }
}
