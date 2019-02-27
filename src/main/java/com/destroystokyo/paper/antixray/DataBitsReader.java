package com.destroystokyo.paper.antixray;

public class DataBitsReader {

    private byte[] dataBits;
    private int bitsPerObject;
    private int mask;
    private int longInDataBitsIndex;
    private int bitInLongIndex;
    private long current;

    public void setDataBits(byte[] dataBits) {
        this.dataBits = dataBits;
    }

    public void setBitsPerObject(int bitsPerObject) {
        this.bitsPerObject = bitsPerObject;
        mask = (1 << bitsPerObject) - 1;
    }

    public void setIndex(int index) {
        this.longInDataBitsIndex = index;
        bitInLongIndex = 0;
        init();
    }

    private void init() {
        if (dataBits.length > longInDataBitsIndex + 7) {
            current = ((((long) dataBits[longInDataBitsIndex]) << 56)
                    | (((long) dataBits[longInDataBitsIndex + 1] & 0xff) << 48)
                    | (((long) dataBits[longInDataBitsIndex + 2] & 0xff) << 40)
                    | (((long) dataBits[longInDataBitsIndex + 3] & 0xff) << 32)
                    | (((long) dataBits[longInDataBitsIndex + 4] & 0xff) << 24)
                    | (((long) dataBits[longInDataBitsIndex + 5] & 0xff) << 16)
                    | (((long) dataBits[longInDataBitsIndex + 6] & 0xff) << 8)
                    | (((long) dataBits[longInDataBitsIndex + 7] & 0xff)));
        }
    }

    public int read() {
        int value = (int) (current >>> bitInLongIndex) & mask;
        bitInLongIndex += bitsPerObject;

        if (bitInLongIndex > 63) {
            bitInLongIndex -= 64;
            longInDataBitsIndex += 8;
            init();

            if (bitInLongIndex > 0) {
                value |= current << bitsPerObject - bitInLongIndex & mask;
            }
        }

        return value;
    }
}
