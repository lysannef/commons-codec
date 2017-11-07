/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Percent codec test cases.
 */
public class PercentCodecTest {

    @Test
    public void testBasicEncodeDecode() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        final String input = "abcdABCD";
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        byte[] decoded = percentCodec.decode(encoded);
        final String decodedS = new String(decoded, "UTF-8");
        assertEquals("Basic PercentCodec encoding test", input, encodedS);
        assertEquals("Basic PercentCodec decoding test", input, decodedS);
    }

    @Test
    @Ignore
    public void testBasicSpace() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        final String input = " ";
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        Assert.assertArrayEquals("%20".getBytes(Charset.forName("UTF-8")), encoded);
    }

    @Test
    public void testSafeCharEncodeDecodeObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec(null, true);
        final String input = "abc123_-.*";
        Object encoded = percentCodec.encode((Object) input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String((byte[]) encoded, "UTF-8");
        Object decoded = percentCodec.decode(encoded);
        final String decodedS = new String((byte[]) decoded, "UTF-8");
        assertEquals("Basic PercentCodec safe char encoding test", input, encodedS);
        assertEquals("Basic PercentCodec safe char decoding test", input, decodedS);
    }

    @Test
    public void testUnsafeCharEncodeDecode() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        final String input = "\u03B1\u03B2\u03B3\u03B4\u03B5\u03B6% ";
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        byte[] decoded = percentCodec.decode(encoded);
        final String decodedS = new String(decoded, "UTF-8");
        assertEquals("Basic PercentCodec unsafe char encoding test", "%CE%B1%CE%B2%CE%B3%CE%B4%CE%B5%CE%B6%25 ", encodedS);
        assertEquals("Basic PercentCodec unsafe char decoding test", input, decodedS);
    }

    @Test
    public void testConfigurablePercentEncoder() throws Exception {
        final String input = "abc123_-.*\u03B1\u03B2";
        PercentCodec percentCodec = new PercentCodec("abcdef".getBytes("UTF-8"), false);
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        assertEquals("Configurable PercentCodec encoding test", "%61%62%63123_-.*%CE%B1%CE%B2", encodedS);
        final byte[] decoded = percentCodec.decode(encoded);
        assertEquals("Configurable PercentCodec decoding test", new String(decoded, "UTF-8"), input);
    }

    @Test
    public void testPercentEncoderDecoderWithNullOrEmptyInput() throws Exception {
        PercentCodec percentCodec = new PercentCodec(null, true);
        assertEquals("Null input value encoding test", percentCodec.encode(null), null);
        assertEquals("Null input value decoding test", percentCodec.decode(null), null);
        byte[] emptyInput = "".getBytes("UTF-8");
        assertEquals("Empty input value encoding test", percentCodec.encode(emptyInput), emptyInput);
        assertTrue("Empty input value decoding test", Arrays.equals(percentCodec.decode(emptyInput), emptyInput));
    }

    @Test
    public void testPercentEncoderDecoderWithPlusForSpace() throws Exception {
        final String input = "a b c d";
        PercentCodec percentCodec = new PercentCodec(null, true);
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        assertEquals("PercentCodec plus for space encoding test", "a+b+c+d", encodedS);
        byte[] decode = percentCodec.decode(encoded);
        assertEquals("PercentCodec plus for space decoding test", new String(decode, "UTF-8"), input);
    }

    @Test(expected = EncoderException.class)
    public void testEncodeUnsupportedObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        percentCodec.encode("test");
    }

    @Test
    public void testEncodeNullObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        assertEquals(percentCodec.encode((Object) null), null);
    }

    @Test(expected = DecoderException.class)
    public void testDecodeUnsupportedObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        percentCodec.decode("test");
    }

    @Test
    public void testDecodeNullObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        assertEquals(percentCodec.decode((Object) null), null);
    }

    @Test
    public void testDecodeInvalidEncodedResultDecoding() throws Exception {
        String inputS = "\u03B1\u03B2";
        PercentCodec percentCodec = new PercentCodec();
        byte[] encoded = percentCodec.encode(inputS.getBytes("UTF-8"));
        try {
            percentCodec.decode(Arrays.copyOf(encoded, encoded.length-1)); //exclude one byte
        } catch (Exception e) {
            assertTrue(DecoderException.class.isInstance(e) &&
                ArrayIndexOutOfBoundsException.class.isInstance(e.getCause()));
        }
    }

}