package com.millionairecodergame.millionairecoder.service.customcodec;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;

public class BooleanArrayCodec implements Codec<boolean[]> {
    @Override
    public void encode(BsonWriter writer, boolean[] value, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (boolean b : value) {
            writer.writeBoolean(b);
        }
        writer.writeEndArray();
    }

    @Override
    public boolean[] decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartArray();
        List<Boolean> booleanList = new ArrayList<>();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            booleanList.add(reader.readBoolean());
        }
        reader.readEndArray();
        boolean[] booleanArray = new boolean[booleanList.size()];
        for (int i = 0; i < booleanList.size(); i++) {
            booleanArray[i] = booleanList.get(i);
        }
        return booleanArray;
    }

    @Override
    public Class<boolean[]> getEncoderClass() {
        return boolean[].class;
    }
}
