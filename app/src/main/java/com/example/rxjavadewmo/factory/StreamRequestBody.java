package com.example.rxjavadewmo.factory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class StreamRequestBody extends RequestBody {

    private byte[] datas;

    public void setDatas(byte[] datas){
        this.datas = datas;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/octet-stream");
    }

    @Override
    public long contentLength() throws IOException {
        return datas.length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(new ByteArrayInputStream(datas));
            sink.writeAll(source);
        } finally {
            Util.closeQuietly(source);
        }
    }
}
