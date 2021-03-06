package yatta.runtime;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class UnicodeUtilsTest {

  private static final byte UTF8_1B = (byte) 0x05;
  private static final byte UTF8_2B = (byte) 0xc5;
  private static final byte UTF8_3B = (byte) 0xe5;
  private static final byte UTF8_4B = (byte) 0xf5;
  private static final byte UTF8_CC = (byte) 0x85;

  @Test
  public void testInt16ReadWrite() {
    final byte[] data = new byte[2];
    UnicodeUtils.int16Write((short) 0, data, 0);
    assertEquals((short) 0, UnicodeUtils.int16Read(data, 0));
    final short[] vals = new short[]{
     0x1, 0x2, 0x4, 0x8,
     0x1a, 0x2a, 0x4a, 0x8a,
     0x1ab, 0x2ab, 0x4ab, 0x8ab,
     0x1abc, 0x2abc, 0x4abc, (short) 0x8abc
    };
    for (short val : vals) {
      UnicodeUtils.int16Write(val, data, 0);
      assertEquals(val, UnicodeUtils.int16Read(data, 0));
    }
  }

  @Test
  public void testInt32ReadWrite() {
    final byte[] data = new byte[4];
    UnicodeUtils.int32Write(0, data, 0);
    assertEquals(0, UnicodeUtils.int32Read(data, 0));
    final int[] vals = new int[]{
     0x1, 0x2, 0x4, 0x8,
     0x1a, 0x2a, 0x4a, 0x8a,
     0x1ab, 0x2ab, 0x4ab, 0x8ab,
     0x1abc, 0x2abc, 0x4abc, 0x8abc,
     0x1abcd, 0x2abcd, 0x4abcd, 0x8abcd,
     0x1abcde, 0x2abcde, 0x4abcde, 0x8abcde,
     0x1abcdef, 0x2abcdef, 0x4abcdef, 0x8abcdef,
     0x1abcdef0, 0x2abcdef0, 0x4abcdef0, 0x8abcdef0
    };
    for (int val : vals) {
      UnicodeUtils.int32Write(val, data, 0);
      assertEquals(val, UnicodeUtils.int32Read(data, 0));
    }
  }

  @Test
  public void testInt64ReadWrite() {
    final byte[] data = new byte[8];
    UnicodeUtils.int64Write(0, data, 0);
    assertEquals(0, UnicodeUtils.int64Read(data, 0));
    final long[] vals = new long[]{
     0x1, 0x2, 0x4, 0x8,
     0x1a, 0x2a, 0x4a, 0x8a,
     0x1ab, 0x2ab, 0x4ab, 0x8ab,
     0x1abc, 0x2abc, 0x4abc, 0x8abc,
     0x1abcd, 0x2abcd, 0x4abcd, 0x8abcd,
     0x1abcde, 0x2abcde, 0x4abcde, 0x8abcde,
     0x1abcdef, 0x2abcdef, 0x4abcdef, 0x8abcdef,
     0x1abcdef0, 0x2abcdef0, 0x4abcdef0, 0x8abcdef0,
     0x1abcdef01L, 0x2abcdef01L, 0x4abcdef01L, 0x8abcdef01L,
     0x1abcdef012L, 0x2abcdef012L, 0x4abcdef012L, 0x8abcdef012L,
     0x1abcdef0123L, 0x2abcdef0123L, 0x4abcdef0123L, 0x8abcdef0123L,
     0x1abcdef01234L, 0x2abcdef01234L, 0x4abcdef01234L, 0x8abcdef01234L,
     0x1abcdef012345L, 0x2abcdef012345L, 0x4abcdef012345L, 0x8abcdef012345L,
     0x1abcdef0123456L, 0x2abcdef0123456L, 0x4abcdef0123456L, 0x8abcdef0123456L,
     0x1abcdef01234567L, 0x2abcdef01234567L, 0x4abcdef01234567L, 0x8abcdef01234567L,
     0x1abcdef012345678L, 0x2abcdef012345678L, 0x4abcdef012345678L, 0x8abcdef012345678L
    };
    for (long val : vals) {
      UnicodeUtils.int64Write(val, data, 0);
      assertEquals(val, UnicodeUtils.int64Read(data, 0));
    }
  }

  @Test
  public void testVarInt63Len() {
    assertEquals(1, UnicodeUtils.varInt63Len(0x0L));
    assertEquals(1, UnicodeUtils.varInt63Len(0x7aL));
    assertEquals(2, UnicodeUtils.varInt63Len(0x8aL));
    assertEquals(2, UnicodeUtils.varInt63Len(0x3abcL));
    assertEquals(3, UnicodeUtils.varInt63Len(0x4abcL));
    assertEquals(3, UnicodeUtils.varInt63Len(0x1abcdeL));
    assertEquals(4, UnicodeUtils.varInt63Len(0x2abcdeL));
    assertEquals(4, UnicodeUtils.varInt63Len(0xfabcdefL));
    assertEquals(5, UnicodeUtils.varInt63Len(0x1abcdef0L));
    assertEquals(5, UnicodeUtils.varInt63Len(0x7abcdef01L));
    assertEquals(6, UnicodeUtils.varInt63Len(0x8abcdef01L));
    assertEquals(6, UnicodeUtils.varInt63Len(0x3abcdef0123L));
    assertEquals(7, UnicodeUtils.varInt63Len(0x4abcdef0123L));
    assertEquals(7, UnicodeUtils.varInt63Len(0x1abcdef012345L));
    assertEquals(8, UnicodeUtils.varInt63Len(0x2abcdef012345L));
    assertEquals(8, UnicodeUtils.varInt63Len(0xfabcdef0123456L));
    assertEquals(9, UnicodeUtils.varInt63Len(0x1abcdef01234567L));
    assertEquals(9, UnicodeUtils.varInt63Len(0x7abcdef012345678L));
  }

  @Test
  public void testVarInt63ReadWrite() {
    final byte[] data = new byte[9];
    UnicodeUtils.varInt63Write(0x7abcdef012345678L, data, 0);
    assertEquals(0x7abcdef012345678L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x1abcdef01234567L, data, 0);
    assertEquals(0x1abcdef01234567L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0xfabcdef0123456L, data, 0);
    assertEquals(0xfabcdef0123456L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x2abcdef012345L, data, 0);
    assertEquals(0x2abcdef012345L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x1abcdef012345L, data, 0);
    assertEquals(0x1abcdef012345L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x4abcdef0123L, data, 0);
    assertEquals(0x4abcdef0123L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x3abcdef0123L, data, 0);
    assertEquals(0x3abcdef0123L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x8abcdef01L, data, 0);
    assertEquals(0x8abcdef01L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x7abcdef01L, data, 0);
    assertEquals(0x7abcdef01L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x1abcdef0, data, 0);
    assertEquals(0x1abcdef0L, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0xfabcdefL, data, 0);
    assertEquals(0xfabcdefL, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x2abcdeL, data, 0);
    assertEquals(0x2abcdeL, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x1abcdeL, data, 0);
    assertEquals(0x1abcdeL, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x4abcL, data, 0);
    assertEquals(0x4abcL, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x3abcL, data, 0);
    assertEquals(0x3abcL, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x8aL, data, 0);
    assertEquals(0x8aL, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x7aL, data, 0);
    assertEquals(0x7aL, UnicodeUtils.varInt63Read(data, 0));
    UnicodeUtils.varInt63Write(0x0L, data, 0);
    assertEquals(0x0L, UnicodeUtils.varInt63Read(data, 0));
  }

  @Test
  public void testUtf8Offset() {
    byte[] bytes;
    // leftmost, U+0000 - U+007F
    bytes = new byte[] { UTF8_1B, 0 };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 0)]);
    // leftmost, U+0080 - U+07FF
    bytes = new byte[] { UTF8_2B, UTF8_CC, 0 };
    assertEquals(UTF8_2B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 0)]);
    // leftmost, U+0800 - U+FFFF
    bytes = new byte[] { UTF8_3B, UTF8_CC, UTF8_CC, 0 };
    assertEquals(UTF8_3B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 0)]);
    // leftmost, U+10000 - U+10FFFF
    bytes = new byte[] { UTF8_4B, UTF8_CC, UTF8_CC, UTF8_CC, 0 };
    assertEquals(UTF8_4B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 0)]);
    // rightmost, U+0000 - U+007F
    bytes = new byte[] { 0, UTF8_1B };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 1)]);
    // rightmost, U+0080 - U+07FF
    bytes = new byte[] { 0, UTF8_2B, UTF8_CC };
    assertEquals(UTF8_2B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 1)]);
    // rightmost, U+0800 - U+FFFF
    bytes = new byte[] { 0, UTF8_3B, UTF8_CC, UTF8_CC };
    assertEquals(UTF8_3B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 1)]);
    // rightmost, U+10000 - U+10FFFF
    bytes = new byte[] { 0, UTF8_4B, UTF8_CC, UTF8_CC, UTF8_CC };
    assertEquals(UTF8_4B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 1)]);
    // mid-left, U+0000 - U+007F
    bytes = new byte[] { 0, UTF8_1B, 0, 0 };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 1)]);
    // mid-left, U+0080 - U+07FF
    bytes = new byte[] { UTF8_2B, UTF8_CC, UTF8_1B, 0, 0 };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 1)]);
    // mid-left, U+0800 - U+FFFF
    bytes = new byte[] { UTF8_3B, UTF8_CC, UTF8_CC, UTF8_1B, 0, 0 };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 1)]);
    // mid-left, U+10000 - U+10FFFF
    bytes = new byte[] { UTF8_4B, UTF8_CC, UTF8_CC, UTF8_CC, UTF8_1B, 0, 0 };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 1)]);
    // mid-right, U+0000 - U+007F
    bytes = new byte[] { 0, 0, UTF8_1B, 0 };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 2)]);
    // mid-right, U+0080 - U+07FF
    bytes = new byte[] { 0, 0, UTF8_1B, UTF8_2B, UTF8_CC };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 2)]);
    // mid-right, U+0800 - U+FFFF
    bytes = new byte[] { 0, 0, UTF8_1B, UTF8_3B, UTF8_CC, UTF8_CC };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 2)]);
    // mid-right, U+10000 - U+10FFFF
    bytes = new byte[] { 0, 0, UTF8_1B, UTF8_4B, UTF8_CC, UTF8_CC, UTF8_CC };
    assertEquals(UTF8_1B, bytes[UnicodeUtils.utf8Offset(bytes, 0, 2)]);
  }

  @Test
  public void testUtf8EncodeDecode() {
    ByteBuffer buffer = ByteBuffer.allocate(4);
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      if (UnicodeUtils.utf8Length(i) != -1) {
        UnicodeUtils.utf8Encode(buffer, i);
        assertEquals(i, UnicodeUtils.utf8Decode(buffer.array(), 0));
        buffer.position(0);
        buffer.putInt(0, 0);
      }
    }
  }
}
