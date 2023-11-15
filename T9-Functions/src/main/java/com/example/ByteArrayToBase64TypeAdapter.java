package com.example;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Base64;

public class ByteArrayToBase64TypeAdapter extends TypeAdapter<byte[]> {
    @Override
    public void write(JsonWriter out, byte[] data) throws IOException {
        if (data == null) {
            out.nullValue();
        } else {
            out.value(Base64.getEncoder().encodeToString(data));
        }
    }

    @Override
    public byte[] read(JsonReader in) throws IOException {
        // Note: this method is not called during serialization, only during deserialization
        return Base64.getDecoder().decode(in.nextString());
    }
}
