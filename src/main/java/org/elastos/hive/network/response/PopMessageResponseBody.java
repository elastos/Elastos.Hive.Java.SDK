package org.elastos.hive.network.response;

import java.util.List;

public class PopMessageResponseBody extends HiveResponseBody {
    private List<Message> messages;

    public List<Message> getMessages() {
        return this.messages;
    }

    public class Message {
        String message;
        Long time;

        public Long getTime() {
            return time;
        }

        public Message setTime(Long time) {
            this.time = time;
            return this;
        }
    }
}
