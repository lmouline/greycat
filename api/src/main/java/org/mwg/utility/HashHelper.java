package org.mwg.utility;

import org.mwg.Constants;

public class HashHelper {

    /**
     * {@native ts
     * public static PRIME1 : Long = Long.fromNumber(2654435761, false);
     * }
     */
    private static final long PRIME1 = 2654435761L;

    /**
     * {@native ts
     * public static PRIME2 : Long = Long.fromNumber(2246822519, false);
     * }
     */
    private static final long PRIME2 = 2246822519L;

    /**
     * {@native ts
     * public static PRIME3 : Long = Long.fromNumber(3266489917, false);
     * }
     */
    private static final long PRIME3 = 3266489917L;

    /**
     * {@native ts
     * public static PRIME4 : Long = Long.fromNumber(668265263, false);
     * }
     */
    private static final long PRIME4 = 668265263L;

    /**
     * {@native ts
     * public static PRIME5 : Long = Long.fromNumber(0x165667b1, false);
     * }
     */
    private static final long PRIME5 = 0x165667b1;

    private static final int len = 24;

    /**
     * {@native ts
     * if (max <= 0) {
     * throw new Error("Max must be > 0");
     * }
     * var crc = org.mwg.utility.HashHelper.PRIME5;
     * crc = crc.add(number);
     * crc = crc.add(crc.shiftLeft(17));
     * crc = crc.mul(org.mwg.utility.HashHelper.PRIME4);
     * crc = crc.mul(org.mwg.utility.HashHelper.PRIME1);
     * crc = crc.add(number);
     * crc = crc.add(crc.shiftLeft(17));
     * crc = crc.mul(org.mwg.utility.HashHelper.PRIME4);
     * crc = crc.mul(org.mwg.utility.HashHelper.PRIME1);
     * crc = crc.add(org.mwg.utility.HashHelper.len);
     * crc = crc.xor(crc.shiftRightUnsigned(15));
     * crc = crc.mul(org.mwg.utility.HashHelper.PRIME2);
     * crc = crc.add(number);
     * crc = crc.xor(crc.shiftRightUnsigned(13));
     * crc = crc.mul(org.mwg.utility.HashHelper.PRIME3);
     * crc = crc.xor(crc.shiftRightUnsigned(16));
     * crc = (crc.isNegative()?crc.mul(-1):crc);
     * crc = crc.mod(max);
     * return crc.toNumber();
     * }
     */
    /**
     * Hashes a long
     * @param number    the long to hash
     * @param max       the max hash value
     * @return          the hash value
     */
    public static long longHash(long number, long max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Max must be > 0");
        }
        long crc = PRIME5;
        crc += number;
        crc += crc << 17;
        crc *= PRIME4;
        crc *= PRIME1;
        crc += number;
        crc += crc << 17;
        crc *= PRIME4;
        crc *= PRIME1;
        crc += len;
        crc ^= crc >>> 15;
        crc *= PRIME2;
        crc += number;
        crc ^= crc >>> 13;
        crc *= PRIME3;
        crc ^= crc >>> 16;

        /*
        //To check later if we can replace by somthing better
        crc = crc & 0x7FFFFFFFFFFFFFFFL; //convert positive
        crc = crc % max;           // return between 0 and max
        */
        crc = (crc < 0 ? crc * -1 : crc); // positive
        crc = crc % max;

        return crc;
    }

    /**
     * {@native ts
     * if (max <= 0) {
     * throw new Error("Max must be > 0");
     * }
     * var v1 = org.mwg.utility.HashHelper.PRIME5;
     * var v2 = v1.mul(org.mwg.utility.HashHelper.PRIME2).add(org.mwg.utility.HashHelper.len);
     * var v3 = v2.mul(org.mwg.utility.HashHelper.PRIME3);
     * var v4 = v3.mul(org.mwg.utility.HashHelper.PRIME4);
     * v1 = v1.shiftLeft(13).or(v1.shiftRightUnsigned(51)).add(Long.fromNumber(p1, false));
     * v2 = v2.shiftLeft(11).or(v2.shiftRightUnsigned(53)).add(Long.fromNumber(p2, false));
     * v3 = v3.shiftLeft(17).or(v3.shiftRightUnsigned(47)).add(Long.fromNumber(p3, false));
     * v4 = v4.shiftLeft(19).or(v4.shiftRightUnsigned(45)).add(Long.fromNumber(p0, false));
     * v1 = v1.add(v1.shiftLeft(17).or(v1.shiftRightUnsigned(47)));
     * v2 = v2.add(v2.shiftLeft(19).or(v2.shiftRightUnsigned(45)));
     * v3 = v3.add(v3.shiftLeft(13).or(v3.shiftRightUnsigned(51)));
     * v4 = v4.add(v4.shiftLeft(11).or(v4.shiftRightUnsigned(53)));
     * v1 = v1.mul(org.mwg.utility.HashHelper.PRIME1).add(Long.fromNumber(p1, false));
     * v2 = v2.mul(org.mwg.utility.HashHelper.PRIME1).add(Long.fromNumber(p2, false));
     * v3 = v3.mul(org.mwg.utility.HashHelper.PRIME1).add(Long.fromNumber(p3, false));
     * v4 = v4.mul(org.mwg.utility.HashHelper.PRIME1).add(org.mwg.utility.HashHelper.PRIME5);
     * v1 = v1.mul(org.mwg.utility.HashHelper.PRIME2);
     * v2 = v2.mul(org.mwg.utility.HashHelper.PRIME2);
     * v3 = v3.mul(org.mwg.utility.HashHelper.PRIME2);
     * v4 = v4.mul(org.mwg.utility.HashHelper.PRIME2);
     * v1 = v1.add(v1.shiftLeft(11).or(v1.shiftRightUnsigned(53)));
     * v2 = v2.add(v2.shiftLeft(17).or(v2.shiftRightUnsigned(47)));
     * v3 = v3.add(v3.shiftLeft(19).or(v3.shiftRightUnsigned(45)));
     * v4 = v4.add(v4.shiftLeft(13).or(v4.shiftRightUnsigned(51)));
     * v1 = v1.mul(org.mwg.utility.HashHelper.PRIME3);
     * v2 = v2.mul(org.mwg.utility.HashHelper.PRIME3);
     * v3 = v3.mul(org.mwg.utility.HashHelper.PRIME3);
     * v4 = v4.mul(org.mwg.utility.HashHelper.PRIME3);
     * var crc = v1;
     * crc = crc.add(v2.shiftLeft(3).or(v2.shiftRightUnsigned(61)));
     * crc = crc.add(v3.shiftLeft(6).or(v3.shiftRightUnsigned(58)));
     * crc = crc.add(v4.shiftLeft(9).or(v4.shiftRightUnsigned(55)));
     * crc = crc.xor(crc.shiftRightUnsigned(11));
     * crc = crc.add(org.mwg.utility.HashHelper.PRIME4.add(org.mwg.utility.HashHelper.len).mul(org.mwg.utility.HashHelper.PRIME1));
     * crc = crc.xor(crc.shiftRightUnsigned(15));
     * crc = crc.mul(org.mwg.utility.HashHelper.PRIME2);
     * crc = crc.xor(crc.shiftRightUnsigned(13));
     * crc = (crc.isNegative()?crc.mul(-1):crc);
     * crc = crc.mod(max);
     * return crc.toNumber();
     * }
     */
    /**
     * Hashes a triple
     * @param p0    First byte
     * @param p1    First long
     * @param p2    Second long
     * @param p3    Third long
     * @param max   the max hash value
     * @return      the hash
     */
    public static long tripleHash(byte p0, long p1, long p2, long p3, long max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Max must be > 0");
        }

        long v1 = PRIME5;
        long v2 = v1 * PRIME2 + len;
        long v3 = v2 * PRIME3;
        long v4 = v3 * PRIME4;

        long crc;

        v1 = ((v1 << 13) | (v1 >>> 51)) + p1;
        v2 = ((v2 << 11) | (v2 >>> 53)) + p2;
        v3 = ((v3 << 17) | (v3 >>> 47)) + p3;
        v4 = ((v4 << 19) | (v4 >>> 45)) + p0;

        v1 += ((v1 << 17) | (v1 >>> 47));
        v2 += ((v2 << 19) | (v2 >>> 45));
        v3 += ((v3 << 13) | (v3 >>> 51));
        v4 += ((v4 << 11) | (v4 >>> 53));

        v1 *= PRIME1;
        v2 *= PRIME1;
        v3 *= PRIME1;
        v4 *= PRIME1;

        v1 += p1;
        v2 += p2;
        v3 += p3;
        v4 += PRIME5;

        v1 *= PRIME2;
        v2 *= PRIME2;
        v3 *= PRIME2;
        v4 *= PRIME2;

        v1 += ((v1 << 11) | (v1 >>> 53));
        v2 += ((v2 << 17) | (v2 >>> 47));
        v3 += ((v3 << 19) | (v3 >>> 45));
        v4 += ((v4 << 13) | (v4 >>> 51));

        v1 *= PRIME3;
        v2 *= PRIME3;
        v3 *= PRIME3;
        v4 *= PRIME3;

        crc = v1 + ((v2 << 3) | (v2 >>> 61)) + ((v3 << 6) | (v3 >>> 58)) + ((v4 << 9) | (v4 >>> 55));
        crc ^= crc >>> 11;
        crc += (PRIME4 + len) * PRIME1;
        crc ^= crc >>> 15;
        crc *= PRIME2;
        crc ^= crc >>> 13;

        /*
        //To check later if we can replace by somthing better
        crc = crc & 0x7FFFFFFFFFFFFFFFL; //convert positive
        crc = crc % max;           // return between 0 and max
        */

        crc = (crc < 0 ? crc * -1 : crc); // positive
        crc = crc % max;

        return crc;
    }


    /**
     * {@native ts
     * return Math.random() * 1000000
     * }
     */
    /**
     * Returns a random long number
     * @return a random number
     */
    public static long rand() {
        return (long) (Math.random() * Constants.END_OF_TIME);
    }

    /**
     * {@native ts
     * return src === other
     * }
     */
    /**
     * Tests equality between two elements
     * @param src       The first element
     * @param other     The second element
     * @return          True if equals, false otherwise
     * @deprecated
     * @see Constants#equals(String, String)
     */
    public static boolean equals(String src, String other) {
        return src.equals(other);
    }

    /**
     * {@native ts
     * return Number.MIN_VALUE;
     * }
     */
    /**
     * Returns the minimum double value
     * @return the minimum double value
     */
    public static double DOUBLE_MIN_VALUE() {
        return Double.MIN_VALUE;
    }

    /**
     * {@native ts
     * return Number.MAX_VALUE;
     * }
     */
    /**
     * Returns the maximum double value
     * @return the maximum double value
     */
    public static double DOUBLE_MAX_VALUE() {
        return Double.MAX_VALUE;
    }

    /**
     * {@native ts
     * return param != undefined && param != null;
     * }
     */
    /**
     * Checks if an object is defined
     * @param param The element to check
     * @return      True if defined, null otherwise.
     * @deprecated
     * @see Constants#isDefined(Object)
     */
    public static boolean isDefined(Object param) {
        return param != null;
    }

    /**
     * {@native ts
     * private static byteTable = function(){
     * var table = [];
     * var h = Long.fromBits(0xCAAF1684, 0x544B2FBA);
     * for (var i = 0; i < 256; i++) {
     * for (var j = 0; j < 31; j++) {
     * h = h.shiftRightUnsigned(7).xor(h);
     * h = h.shiftLeft(11).xor(h);
     * h = h.shiftRightUnsigned(10).xor(h);
     * }
     * table[i] = h.toSigned();
     * }
     * return table;
     * }();
     * }
     */
    private static final long[] byteTable = createLookupTable();

    /**
     * {@native ts
     * private static HSTART : Long = Long.fromBits(0xA205B064, 0xBB40E64D);
     * }
     */
    private static final long HSTART = 0xBB40E64DA205B064L;
    //0xBB40E64DA205B064

    /**
     * {@native ts
     * private static HMULT : Long = Long.fromBits(0xE116586D,0x6A5D39EA);
     * }
     */
    private static final long HMULT = 7664345821815920749L;
    //0x6A5D39EAE116586D


    /**
     * {@native ts
     * var h = org.mwg.utility.HashHelper.HSTART;
     * var dataLength = data.length;
     * for (var i = 0; i < dataLength; i++) {
     * h = h.mul(org.mwg.utility.HashHelper.HMULT).xor(org.mwg.utility.HashHelper.byteTable[data.charCodeAt(i) & 0xff]);
     * }
     * return h.mod(org.mwg.core.CoreConstants.END_OF_TIME).toNumber();
     * }
     */
    /**
     * Hashes a String
     * @param data  The string to has
     * @return      The hash value
     */
    public static long hash(String data) {
        long h = HSTART;
        final long hmult = HMULT;
        final long[] ht = byteTable;
        int dataLength = data.length();
        for (int i = 0; i < dataLength; i++) {
            h = (h * hmult) ^ ht[data.codePointAt(i) & 0xff];
        }
        return h % Constants.END_OF_TIME;
    }

    /**
     * {@native ts
     * var h = org.mwg.utility.HashHelper.HSTART;
     * var dataLength = data.length;
     * for (var i = 0; i < dataLength; i++) {
     * h = h.mul(org.mwg.utility.HashHelper.HMULT).xor(org.mwg.utility.HashHelper.byteTable[data[i] & 0xff]);
     * }
     * return h.mod(org.mwg.core.CoreConstants.END_OF_TIME).toNumber();
     * }
     */
    /**
     * Hashes a byte array.
     * @param data  The bytes to hash
     * @return      The hash value
     */
    public static long hashBytes(byte[] data) {
        long h = HSTART;
        final long hmult = HMULT;
        final long[] ht = byteTable;
        int dataLength = data.length;
        for (int i = 0; i < dataLength; i++) {
            h = (h * hmult) ^ ht[data[i] & 0xff];
        }
        return h % Constants.END_OF_TIME;
    }


    /**
     * @ignore ts
     */
    private static final long[] createLookupTable() {
        long[] byteTable = new long[256];
        long h = 0x544B2FBACAAF1684L;
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 31; j++) {
                h = (h >>> 7) ^ h;
                h = (h << 11) ^ h;
                h = (h >>> 10) ^ h;
            }
            byteTable[i] = h;
        }
        return byteTable;
    }


}
