package org.elastos.hive.vendor.connection;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class InputStreamRequestBody extends RequestBody {
    MediaType contentType;
    InputStream inputStream;

    public InputStreamRequestBody(@Nullable MediaType contentType, InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException("file == null");
        }
        this.contentType = contentType;
        this.inputStream = inputStream;
    }
    public long contentLength() {
        return 0;
    }


    @Override
    public MediaType contentType() {
        return MediaType.parse("application/octet-stream");
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        Source source = Okio.source(inputStream);
        bufferedSink.writeAll(source);
    }
}
