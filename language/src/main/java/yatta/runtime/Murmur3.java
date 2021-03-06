package yatta.runtime;

public final class Murmur3 extends Hasher {
  static final long C1 = 0x87c37b91114253d5L;
  static final long C2 = 0x4cf5ad432745937fL;

  public static final Murmur3 INSTANCE = new Murmur3();

  private Murmur3() {}

  @Override
  public long hash(final long seed, final Object o) {
    if (o instanceof Boolean) {
      return hashBool(seed, (Boolean) o);
    } else if (o instanceof Byte) {
      return hashByte(seed, (Byte) o);
    } else if (o instanceof Integer) {
      return hashChar(seed, (Integer) o);
    } else if (o instanceof Long) {
      return hashInteger(seed, (Long) o);
    } else if (o instanceof Double) {
      return hashFloat(seed, (Double) o);
    } else if (o instanceof Object[]) {
      return hashTuple(seed, (Object[]) o);
    } else if (o instanceof Seq) {
      return ((Seq) o).murmur3Hash(seed);
    } else if (o instanceof Set) {
      return ((Set) o).murmur3Hash(seed);
    } else {
      final int hash = o.hashCode();
      return seed ^ ((((long) hash) << 32) | (hash & 0xffffffffL));
    }
  }

  static long hashBool(final long seed, final boolean value) {
    return seed ^ (value ? 1695892510400506682L : 214721695803549140L);
  }

  static long hashByte(final long seed, final byte value) {
    long hash = 0xffL & value;
    hash *= C1;
    hash = Long.rotateLeft(hash, 31);
    hash *= C2;
    hash ^= seed;
    return fMix64(hash ^ 1);
  }

  static long hashChar(final long seed, final int value) {
    long hash = 0xffffffffL & Integer.reverseBytes(value);
    hash *= C1;
    hash = Long.rotateLeft(hash, 31);
    hash *= C2;
    hash ^= seed;
    return fMix64(hash ^ 4);
  }

  static long hashInteger(final long seed, final long value) {
    long hash = Long.reverseBytes(value);
    hash *= C1;
    hash = Long.rotateLeft(hash, 31);
    hash *= C2;
    hash ^= seed;
    hash = Long.rotateLeft(hash, 27) * 5 + 0x52dce729;
    return fMix64(hash ^ 8);
  }

  static long hashFloat(final long seed, final double value) {
    return hashInteger(seed, Double.doubleToLongBits(value));
  }

  static long hashTuple(final long seed, final Object[] values) {
    long hash = seed;
    for (Object value : values) {
      long k = INSTANCE.hash(seed, value);
      k *= C1;
      k = Long.rotateLeft(k, 31);
      k *= C2;
      hash ^= k;
      hash = Long.rotateLeft(hash, 27) * 5 + 0x52dce729;
    }
    return fMix64(hash ^ values.length);
  }

  static long fMix64(long hash) {
    hash ^= (hash >>> 33);
    hash *= 0xff51afd7ed558ccdL;
    hash ^= (hash >>> 33);
    hash *= 0xc4ceb9fe1a85ec53L;
    hash ^= (hash >>> 33);
    return hash;
  }
}
