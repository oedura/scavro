package org.oedura.scavro;


import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

/*
 * Mock idl Number class to test AvroReader and AvroWriter
 */
public class MockNumber extends SpecificRecordBase implements SpecificRecord {
    /* We allow direct access for testing purposes */
    public CharSequence name;
    public int value;

    public MockNumber(CharSequence name, int value) {
        this.name = name;
        this.value = value;
    }

    public static Schema getClassSchema() {
        return SCHEMA$;
    }

    public static final org.apache.avro.Schema SCHEMA$ = new Schema.Parser().parse(
        "{\n" +
        "  \"type\": \"record\",\n" +
        "  \"name\": \"Number\",\n" +
        "  \"fields\": [\n" +
        "    {\n" +
        "      \"name\": \"name\",\n" +
        "      \"type\": \"string\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"value\",\n" +
        "      \"type\": \"int\"\n" +
        "    }\n" +
        "  ]\n" +
        "}"
    );

    @Override
    public Schema getSchema() {
        return MockNumber.getClassSchema();
    }

    @Override
    public Object get(int field) {
        if (field == 0) return name;
        else if (field == 1) return value;
        else throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public void put(int field, Object value) {
        if (field == 0) this.name = (CharSequence)value;
        else if (field == 1) this.value = (int)value;
        else throw new ArrayIndexOutOfBoundsException();
    }
}
