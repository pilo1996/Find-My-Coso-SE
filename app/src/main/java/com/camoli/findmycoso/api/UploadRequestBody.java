package com.camoli.findmycoso.api;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class UploadRequestBody extends RequestBody {

    private static final int DEFAULT_BUFFER_SIZE = 1048;
    private File file;
    private String contentType;
    private UploadCallback uploadCallback;

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType+"/*");
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Long length = file.length();
        Byte[] buffer = new Byte[DEFAULT_BUFFER_SIZE];
        FileInputStream fileInputStream = new FileInputStream(file);
        Long uploaded = 0L;
    }
}
