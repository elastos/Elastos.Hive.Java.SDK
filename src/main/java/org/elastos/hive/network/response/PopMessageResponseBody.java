package org.elastos.hive.network.response;

import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.network.model.ChannelMessage;

import java.util.List;

public class PopMessageResponseBody extends HiveResponseBody {
    private List<ChannelMessage> messages;

    public List<ChannelMessage> getMessages() {
        return this.messages;
    }
}
