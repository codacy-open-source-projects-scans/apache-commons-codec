/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.digest;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.Checksum;

/**
 * CRC-16 checksum implementations you can customize with a table and init value.
 * <p>
 * Since there are so many CRC-16 variants, we do not pick a default.
 * </p>
 * <p>
 * For example, to create a create a custom variant of CRC16-MODBUS with an init value of {@code 0x0000}, use:
 * </p>
 *
 * <pre>
 * Checksum crc16 = CRC16.builder().setTable(CRC16.getModbusTable()).setInit(0x0000).get();
 * </pre>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Cyclic_redundancy_check">Cyclic redundancy check</a>
 * @see <a href="https://reveng.sourceforge.io/crc-catalogue/16.htm">Catalogue of parametrised CRC algorithms with 16 bits</a>
 * @see <a href="https://www.lammertbies.nl/comm/info/crc-calculation">On-line CRC calculation and free library</a>
 * @see <a href="https://crccalc.com/?crc=123456789&method=&datatype=ascii&outtype=hex">crccalc</a>
 * @since 1.20.0
 */
public final class Crc16 implements Checksum {

    /**
     * Builds {@link Crc16} instances.
     */
    public static final class Builder implements Supplier<Crc16> {

        private int init;
        private int[] table;
        private int xorOut;

        /**
         * Constructs a new instance.
         */
        public Builder() {
            // empty
        }

        /**
         * Creates a new {@link Crc16} instance.
         */
        @Override
        public Crc16 get() {
            return new Crc16(this);
        }

        /**
         * Sets the initial value.
         *
         * @param init the initial value.
         * @return {@code this} instance.
         */
        public Builder setInit(final int init) {
            this.init = init;
            return this;
        }

        /**
         * Sets the lookup table.
         *
         * @param table the lookup table, making a clone of the input array, must not be null.
         * @return {@code this} instance.
         */
        public Builder setTable(final int[] table) {
            return table(Objects.requireNonNull(table, "table").clone());
        }

        /**
         * Sets the XorOut value to XOR to the current checksum returned by {@link Crc16#getValue()}.
         *
         * @param xorOut the XorOut value.
         * @return {@code this} instance.
         */
        public Builder setXorOut(final int xorOut) {
            this.xorOut = xorOut;
            return this;
        }

        /**
         * Sets the lookup table without making a clone.
         *
         * @param table the lookup table, must not be null.
         * @return {@code this} instance.
         */
        private Builder table(final int[] table) {
            this.table = Objects.requireNonNull(table, "table");
            return this;
        }
    }

    // @formatter:off
    private static final int[] ARC = {
        0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
        0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
        0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
        0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
        0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
        0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
        0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
        0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
        0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
        0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
        0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
        0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
        0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
        0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
        0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
        0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
        0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
        0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
        0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
        0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
        0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
        0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
        0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
        0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
        0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
        0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
        0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
        0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
        0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
        0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
        0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
        0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040
    };
    private static final int ARC_INIT = 0x0000;
    private static final int[] CCITT = {
        0x0000, 0x1189, 0x2312, 0x329B, 0x4624, 0x57AD, 0x6536, 0x74BF,
        0x8C48, 0x9DC1, 0xAF5A, 0xBED3, 0xCA6C, 0xDBE5, 0xE97E, 0xF8F7,
        0x1081, 0x0108, 0x3393, 0x221A, 0x56A5, 0x472C, 0x75B7, 0x643E,
        0x9CC9, 0x8D40, 0xBFDB, 0xAE52, 0xDAED, 0xCB64, 0xF9FF, 0xE876,
        0x2102, 0x308B, 0x0210, 0x1399, 0x6726, 0x76AF, 0x4434, 0x55BD,
        0xAD4A, 0xBCC3, 0x8E58, 0x9FD1, 0xEB6E, 0xFAE7, 0xC87C, 0xD9F5,
        0x3183, 0x200A, 0x1291, 0x0318, 0x77A7, 0x662E, 0x54B5, 0x453C,
        0xBDCB, 0xAC42, 0x9ED9, 0x8F50, 0xFBEF, 0xEA66, 0xD8FD, 0xC974,
        0x4204, 0x538D, 0x6116, 0x709F, 0x0420, 0x15A9, 0x2732, 0x36BB,
        0xCE4C, 0xDFC5, 0xED5E, 0xFCD7, 0x8868, 0x99E1, 0xAB7A, 0xBAF3,
        0x5285, 0x430C, 0x7197, 0x601E, 0x14A1, 0x0528, 0x37B3, 0x263A,
        0xDECD, 0xCF44, 0xFDDF, 0xEC56, 0x98E9, 0x8960, 0xBBFB, 0xAA72,
        0x6306, 0x728F, 0x4014, 0x519D, 0x2522, 0x34AB, 0x0630, 0x17B9,
        0xEF4E, 0xFEC7, 0xCC5C, 0xDDD5, 0xA96A, 0xB8E3, 0x8A78, 0x9BF1,
        0x7387, 0x620E, 0x5095, 0x411C, 0x35A3, 0x242A, 0x16B1, 0x0738,
        0xFFCF, 0xEE46, 0xDCDD, 0xCD54, 0xB9EB, 0xA862, 0x9AF9, 0x8B70,
        0x8408, 0x9581, 0xA71A, 0xB693, 0xC22C, 0xD3A5, 0xE13E, 0xF0B7,
        0x0840, 0x19C9, 0x2B52, 0x3ADB, 0x4E64, 0x5FED, 0x6D76, 0x7CFF,
        0x9489, 0x8500, 0xB79B, 0xA612, 0xD2AD, 0xC324, 0xF1BF, 0xE036,
        0x18C1, 0x0948, 0x3BD3, 0x2A5A, 0x5EE5, 0x4F6C, 0x7DF7, 0x6C7E,
        0xA50A, 0xB483, 0x8618, 0x9791, 0xE32E, 0xF2A7, 0xC03C, 0xD1B5,
        0x2942, 0x38CB, 0x0A50, 0x1BD9, 0x6F66, 0x7EEF, 0x4C74, 0x5DFD,
        0xB58B, 0xA402, 0x9699, 0x8710, 0xF3AF, 0xE226, 0xD0BD, 0xC134,
        0x39C3, 0x284A, 0x1AD1, 0x0B58, 0x7FE7, 0x6E6E, 0x5CF5, 0x4D7C,
        0xC60C, 0xD785, 0xE51E, 0xF497, 0x8028, 0x91A1, 0xA33A, 0xB2B3,
        0x4A44, 0x5BCD, 0x6956, 0x78DF, 0x0C60, 0x1DE9, 0x2F72, 0x3EFB,
        0xD68D, 0xC704, 0xF59F, 0xE416, 0x90A9, 0x8120, 0xB3BB, 0xA232,
        0x5AC5, 0x4B4C, 0x79D7, 0x685E, 0x1CE1, 0x0D68, 0x3FF3, 0x2E7A,
        0xE70E, 0xF687, 0xC41C, 0xD595, 0xA12A, 0xB0A3, 0x8238, 0x93B1,
        0x6B46, 0x7ACF, 0x4854, 0x59DD, 0x2D62, 0x3CEB, 0x0E70, 0x1FF9,
        0xF78F, 0xE606, 0xD49D, 0xC514, 0xB1AB, 0xA022, 0x92B9, 0x8330,
        0x7BC7, 0x6A4E, 0x58D5, 0x495C, 0x3DE3, 0x2C6A, 0x1EF1, 0x0F78
    };
    private static final int CCITT_INIT = 0x0000;
    private static final int[] DNP = {
        0x0000, 0x365E, 0x6CBC, 0x5AE2, 0xD978, 0xEF26, 0xB5C4, 0x839A,
        0xFF89, 0xC9D7, 0x9335, 0xA56B, 0x26F1, 0x10AF, 0x4A4D, 0x7C13,
        0xB26B, 0x8435, 0xDED7, 0xE889, 0x6B13, 0x5D4D, 0x07AF, 0x31F1,
        0x4DE2, 0x7BBC, 0x215E, 0x1700, 0x949A, 0xA2C4, 0xF826, 0xCE78,
        0x29AF, 0x1FF1, 0x4513, 0x734D, 0xF0D7, 0xC689, 0x9C6B, 0xAA35,
        0xD626, 0xE078, 0xBA9A, 0x8CC4, 0x0F5E, 0x3900, 0x63E2, 0x55BC,
        0x9BC4, 0xAD9A, 0xF778, 0xC126, 0x42BC, 0x74E2, 0x2E00, 0x185E,
        0x644D, 0x5213, 0x08F1, 0x3EAF, 0xBD35, 0x8B6B, 0xD189, 0xE7D7,
        0x535E, 0x6500, 0x3FE2, 0x09BC, 0x8A26, 0xBC78, 0xE69A, 0xD0C4,
        0xACD7, 0x9A89, 0xC06B, 0xF635, 0x75AF, 0x43F1, 0x1913, 0x2F4D,
        0xE135, 0xD76B, 0x8D89, 0xBBD7, 0x384D, 0x0E13, 0x54F1, 0x62AF,
        0x1EBC, 0x28E2, 0x7200, 0x445E, 0xC7C4, 0xF19A, 0xAB78, 0x9D26,
        0x7AF1, 0x4CAF, 0x164D, 0x2013, 0xA389, 0x95D7, 0xCF35, 0xF96B,
        0x8578, 0xB326, 0xE9C4, 0xDF9A, 0x5C00, 0x6A5E, 0x30BC, 0x06E2,
        0xC89A, 0xFEC4, 0xA426, 0x9278, 0x11E2, 0x27BC, 0x7D5E, 0x4B00,
        0x3713, 0x014D, 0x5BAF, 0x6DF1, 0xEE6B, 0xD835, 0x82D7, 0xB489,
        0xA6BC, 0x90E2, 0xCA00, 0xFC5E, 0x7FC4, 0x499A, 0x1378, 0x2526,
        0x5935, 0x6F6B, 0x3589, 0x03D7, 0x804D, 0xB613, 0xECF1, 0xDAAF,
        0x14D7, 0x2289, 0x786B, 0x4E35, 0xCDAF, 0xFBF1, 0xA113, 0x974D,
        0xEB5E, 0xDD00, 0x87E2, 0xB1BC, 0x3226, 0x0478, 0x5E9A, 0x68C4,
        0x8F13, 0xB94D, 0xE3AF, 0xD5F1, 0x566B, 0x6035, 0x3AD7, 0x0C89,
        0x709A, 0x46C4, 0x1C26, 0x2A78, 0xA9E2, 0x9FBC, 0xC55E, 0xF300,
        0x3D78, 0x0B26, 0x51C4, 0x679A, 0xE400, 0xD25E, 0x88BC, 0xBEE2,
        0xC2F1, 0xF4AF, 0xAE4D, 0x9813, 0x1B89, 0x2DD7, 0x7735, 0x416B,
        0xF5E2, 0xC3BC, 0x995E, 0xAF00, 0x2C9A, 0x1AC4, 0x4026, 0x7678,
        0x0A6B, 0x3C35, 0x66D7, 0x5089, 0xD313, 0xE54D, 0xBFAF, 0x89F1,
        0x4789, 0x71D7, 0x2B35, 0x1D6B, 0x9EF1, 0xA8AF, 0xF24D, 0xC413,
        0xB800, 0x8E5E, 0xD4BC, 0xE2E2, 0x6178, 0x5726, 0x0DC4, 0x3B9A,
        0xDC4D, 0xEA13, 0xB0F1, 0x86AF, 0x0535, 0x336B, 0x6989, 0x5FD7,
        0x23C4, 0x159A, 0x4F78, 0x7926, 0xFABC, 0xCCE2, 0x9600, 0xA05E,
        0x6E26, 0x5878, 0x029A, 0x34C4, 0xB75E, 0x8100, 0xDBE2, 0xEDBC,
        0x91AF, 0xA7F1, 0xFD13, 0xCB4D, 0x48D7, 0x7E89, 0x246B, 0x1235
    };
    private static final int DNP_INIT = 0x0000;
    private static final int DNP_XOROUT = 0xFFFF;
    private static final int[] IBM_SDLC = CCITT;
    private static final int IBM_SDLC_INIT = 0xFFFF;
    private static final int IBM_SDLC_XOROUT = 0xFFFF;
    private static final int[] MAXIM = ARC;
    private static final int MAXIM_INIT = 0x0000;
    private static final int MAXIM_XOROUT = 0xFFFF;
    private static final int[] MCRF4XX = CCITT;
    private static final int MCRF4XX_INIT = 0xFFFF;
    private static final int[] MODBUS = ARC;
    private static final int MODBUS_INIT = 0xFFFF;
    private static final int[] NRSC5 = {
        0x0000, 0x35A4, 0x6B48, 0x5EEC, 0xD690, 0xE334, 0xBDD8, 0x887C,
        0x0D01, 0x38A5, 0x6649, 0x53ED, 0xDB91, 0xEE35, 0xB0D9, 0x857D,
        0x1A02, 0x2FA6, 0x714A, 0x44EE, 0xCC92, 0xF936, 0xA7DA, 0x927E,
        0x1703, 0x22A7, 0x7C4B, 0x49EF, 0xC193, 0xF437, 0xAADB, 0x9F7F,
        0x3404, 0x01A0, 0x5F4C, 0x6AE8, 0xE294, 0xD730, 0x89DC, 0xBC78,
        0x3905, 0x0CA1, 0x524D, 0x67E9, 0xEF95, 0xDA31, 0x84DD, 0xB179,
        0x2E06, 0x1BA2, 0x454E, 0x70EA, 0xF896, 0xCD32, 0x93DE, 0xA67A,
        0x2307, 0x16A3, 0x484F, 0x7DEB, 0xF597, 0xC033, 0x9EDF, 0xAB7B,
        0x6808, 0x5DAC, 0x0340, 0x36E4, 0xBE98, 0x8B3C, 0xD5D0, 0xE074,
        0x6509, 0x50AD, 0x0E41, 0x3BE5, 0xB399, 0x863D, 0xD8D1, 0xED75,
        0x720A, 0x47AE, 0x1942, 0x2CE6, 0xA49A, 0x913E, 0xCFD2, 0xFA76,
        0x7F0B, 0x4AAF, 0x1443, 0x21E7, 0xA99B, 0x9C3F, 0xC2D3, 0xF777,
        0x5C0C, 0x69A8, 0x3744, 0x02E0, 0x8A9C, 0xBF38, 0xE1D4, 0xD470,
        0x510D, 0x64A9, 0x3A45, 0x0FE1, 0x879D, 0xB239, 0xECD5, 0xD971,
        0x460E, 0x73AA, 0x2D46, 0x18E2, 0x909E, 0xA53A, 0xFBD6, 0xCE72,
        0x4B0F, 0x7EAB, 0x2047, 0x15E3, 0x9D9F, 0xA83B, 0xF6D7, 0xC373,
        0xD010, 0xE5B4, 0xBB58, 0x8EFC, 0x0680, 0x3324, 0x6DC8, 0x586C,
        0xDD11, 0xE8B5, 0xB659, 0x83FD, 0x0B81, 0x3E25, 0x60C9, 0x556D,
        0xCA12, 0xFFB6, 0xA15A, 0x94FE, 0x1C82, 0x2926, 0x77CA, 0x426E,
        0xC713, 0xF2B7, 0xAC5B, 0x99FF, 0x1183, 0x2427, 0x7ACB, 0x4F6F,
        0xE414, 0xD1B0, 0x8F5C, 0xBAF8, 0x3284, 0x0720, 0x59CC, 0x6C68,
        0xE915, 0xDCB1, 0x825D, 0xB7F9, 0x3F85, 0x0A21, 0x54CD, 0x6169,
        0xFE16, 0xCBB2, 0x955E, 0xA0FA, 0x2886, 0x1D22, 0x43CE, 0x766A,
        0xF317, 0xC6B3, 0x985F, 0xADFB, 0x2587, 0x1023, 0x4ECF, 0x7B6B,
        0xB818, 0x8DBC, 0xD350, 0xE6F4, 0x6E88, 0x5B2C, 0x05C0, 0x3064,
        0xB519, 0x80BD, 0xDE51, 0xEBF5, 0x6389, 0x562D, 0x08C1, 0x3D65,
        0xA21A, 0x97BE, 0xC952, 0xFCF6, 0x748A, 0x412E, 0x1FC2, 0x2A66,
        0xAF1B, 0x9ABF, 0xC453, 0xF1F7, 0x798B, 0x4C2F, 0x12C3, 0x2767,
        0x8C1C, 0xB9B8, 0xE754, 0xD2F0, 0x5A8C, 0x6F28, 0x31C4, 0x0460,
        0x811D, 0xB4B9, 0xEA55, 0xDFF1, 0x578D, 0x6229, 0x3CC5, 0x0961,
        0x961E, 0xA3BA, 0xFD56, 0xC8F2, 0x408E, 0x752A, 0x2BC6, 0x1E62,
        0x9B1F, 0xAEBB, 0xF057, 0xC5F3, 0x4D8F, 0x782B, 0x26C7, 0x1363
    };
    // @formatter:off
    private static final int NRSC5_INIT = 0xFFFF;
    private static final int[] USB = ARC;
    private static final int USB_INIT = 0xFFFF;
    private static final int USB_XOROUT = 0xFFFF;

    /**
     * Creates a new CRC16-CCITT Checksum.
     *
     * <ul>
     * <li>The init value is {@code 0x0000}.</li>
     * <li>The XorOut value is {@code 0x0000}.</li>
     * </ul>
     * <p>
     * Also known as:
     * </p>
     * <ul>
     * <li>CRC-16/ARC</li>
     * <li>ARC</li>
     * <li>CRC-16</li>
     * <li>CRC-16/LHA</li>
     * <li>CRC-IBM</li>
     * </ul>
     *
     * @return a new CRC16-CCITT Checksum.
     */
    public static Crc16 arc() {
        return builder().setInit(ARC_INIT).table(ARC).get();
    }

    /**
     * Creates a new builder.
     *
     * <p>
     * Since there are so many CRC-16 variants, we do not pick a default.
     * </p>
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new CRC16-CCITT Checksum.
     * <ul>
     * <li>The init value is {@code 0x0000}.</li>
     * <li>The XorOut value is {@code 0x0000}.</li>
     * </ul>
     * <p>
     * Also known as:
     * </p>
     * <ul>
     * <li>CRC-16/KERMIT</li>
     * <li>CRC-16/BLUETOOTH</li>
     * <li>CRC-16/CCITT</li>
     * <li>CRC-16/CCITT-TRUE</li>
     * <li>CRC-16/V-41-LSB</li>
     * <li>CRC-CCITT</li>
     * <li>KERMIT</li>
     * </ul>
     *
     * @return a new CRC16-CCITT Checksum.
     */
    public static Crc16 ccitt() {
        return builder().setInit(CCITT_INIT).table(CCITT).get();
    }

    /**
     * Creates a new CRC16-DNP Checksum.
     *
     * <ul>
     * <li>The init value is {@code 0x0000}.</li>
     * <li>The XorOut value is {@code 0xFFFF}.</li>
     * </ul>
     *
     * @return a new CRC16-DNP Checksum.
     */
    public static Crc16 dnp() {
        return builder().setInit(DNP_INIT).setXorOut(DNP_XOROUT).table(DNP).get();
    }

    /**
     * Gets a copy of the CRC16-CCITT table.
     *
     * @return a copy of the CCRC16-CITT table.
     */
    public static int[] getArcTable() {
        return ARC.clone();
    }

    /**
     * Gets a copy of the CRC16-CCITT table.
     *
     * @return a copy of the CCRC16-CITT table.
     */
    public static int[] getCcittTable() {
        return CCITT.clone();
    }

    /**
     * Gets a copy of the CRC16-DNP table.
     *
     * @return a copy of the CCRC16-DNP table.
     */
    public static int[] getDnpTable() {
        return DNP.clone();
    }

    /**
     * Gets a copy of the CRC16-IBM-SDLC table.
     *
     * @return a copy of the CRC16-IBM-SDLC table.
     */
    public static int[] getIbmSdlcTable() {
        return IBM_SDLC.clone();
    }

    /**
     * Gets a copy of the CRC16-MAXIM table.
     *
     * @return a copy of the CRC16-MAXIM table.
     */
    public static int[] getMaximTable() {
        return MAXIM.clone();
    }

    /**
     * Gets a copy of the CRC16-MCRF4XX table.
     *
     * @return a copy of the CRC16-MCRF4XX table.
     */
    public static int[] getMcrf4xxTable() {
        return MCRF4XX.clone();
    }

    /**
     * Gets a copy of the CRC16-MODBUS table.
     *
     * @return a copy of the CRC16-MODBUS table.
     */
    public static int[] getModbusTable() {
        return MODBUS.clone();
    }

    /**
     * Gets a copy of the CRC16-NRSC-5 table.
     *
     * @return a copy of the CRC16-NRSC-5 table.
     */
    public static int[] getNrsc5Table() {
        return NRSC5.clone();
    }

    /**
     * Creates a new CRC16-IBM-SDLC Checksum.
     *
     * <ul>
     * <li>The init value is {@code 0xFFFF}.</li>
     * <li>The XorOut value is {@code 0xFFFF}.</li>
     * </ul>
     * <p>
     * Also known as:
     * </p>
     * <ul>
     * <li>CRC-16/IBM-SDLC</li>
     * <li>CRC-16/ISO-HDLC</li>
     * <li>CRC-16/ISO-IEC-14443-3-B</li>
     * <li>CRC-16/X-25</li>
     * <li>CRC-B</li>
     * <li>X-25</li>
     * </ul>
     *
     * @return a new CRC16-IBM-SDLC Checksum.
     */
    public static Crc16 ibmSdlc() {
        return builder().setInit(IBM_SDLC_INIT).setXorOut(IBM_SDLC_XOROUT).table(IBM_SDLC).get();
    }

    /**
     * Creates a new instance for CRC16-MAXIM Checksum.
     *
     * <p>
     * CRC-16 checksum implementation based on polynomial {@code x<sup>16</spu> + x^15 + x^2 + 1 (0x8005)}.
     * </p>
     * <ul>
     * <li>The init value is {@code 0xFFFF}.</li>
     * <li>The XorOut value is {@code 0xFFFF}.</li>
     * </ul>
     * <p>
     * Also known as:
     * </p>
     * <ul>
     * <li>CRC-16/MAXIM-DOW</li>
     * </ul>
     *
     * @return a new CRC16-MAXIM Checksum.
     */
    public static Crc16 maxim() {
        return builder().setInit(MAXIM_INIT).setXorOut(MAXIM_XOROUT).table(MAXIM).get();
    }

    /**
     * Creates a new instance for CRC16-MCRF4XX Checksum.
     *
     * <ul>
     * <li>The init value is {@code 0xFFFF}.</li>
     * <li>The XorOut value is {@code 0x0000}.</li>
     * </ul>
     *
     * @return a new CRC16-MCRF4XX Checksum.
     */
    public static Crc16 mcrf4xx() {
        return builder().setInit(MCRF4XX_INIT).table(MCRF4XX).get();
    }

    /**
     * Creates a new instance for CRC16-MODBUS Checksum.
     *
     * <p>
     * CRC-16 checksum implementation based on polynomial {@code x<sup>16</spu> + x^15 + x^2 + 1 (0x8005)}.
     * </p>
     * <ul>
     * <li>The init value is {@code 0xFFFF}.</li>
     * <li>The XorOut value is {@code 0x0000}.</li>
     * </ul>
     * <p>
     * Also known as:
     * </p>
     * <ul>
     * <li>CRC-16/MODBUST</li>
     * <li>MODBUST</li>
     * </ul>
     *
     * @return a new CRC16-MODBUS Checksum.
     */
    public static Crc16 modbus() {
        return builder().setInit(MODBUS_INIT).table(MODBUS).get();
    }

    /**
     * Creates a new instance for CRC16-NRSC-5 Checksum.
     *
     * <ul>
     * <li>The init value is {@code 0xFFFF}.</li>
     * <li>The XorOut value is {@code 0x0000}.</li>
     * </ul>
     *
     * @return a new CRC16-NRSC-5 Checksum.
     */
    public static Crc16 nrsc5() {
        return builder().setInit(NRSC5_INIT).table(NRSC5).get();
    }

    /**
     * Creates a new instance for CRC16-USB Checksum.
     *
     * <ul>
     * <li>The init value is {@code 0xFFFF}.</li>
     * <li>The XorOut value is {@code 0xFFFF}.</li>
     * </ul>
     *
     * @return a new CRC16-USB Checksum.
     */
    public static Crc16 usb() {
        return builder().setInit(USB_INIT).setXorOut(USB_XOROUT).table(USB).get();
    }

    /**
     * CRC.
     */
    private int crc;
    private final int init;
    private final int[] table;
    private final int xorOut;

    /**
     * Constructs a new instance.
     */
    private Crc16(final Builder builder) {
        this.init = builder.init;
        this.xorOut = builder.xorOut;
        this.crc = builder.init;
        this.table = Objects.requireNonNull(builder.table, "table");
    }

    @Override
    public long getValue() {
        return crc ^ xorOut;
    }

    @Override
    public void reset() {
        crc = init;
    }

    @Override
    public String toString() {
        return String.format("%s [init=0x%04X, crc=0x%04X, xorOut=0x%04X, crc^xorOut=0x%04X]", getClass().getSimpleName(), init, crc, xorOut, getValue());
    }

    @Override
    public void update(final byte[] b, final int off, final int len) {
        final int end = len + off;
        for (int i = off; i < end; i++) {
            update(b[i]);
        }
    }

    @Override
    public void update(final int b) {
        crc = crc >>> 8 ^ table[(crc ^ b) & 0xff];
    }
}
